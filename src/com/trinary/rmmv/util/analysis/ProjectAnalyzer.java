package com.trinary.rmmv.util.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig.Feature;

import com.trinary.rmmv.client.PluginVersionClient;
import com.trinary.rmmv.client.RMMVClientConfig;
import com.trinary.rmmv.util.analysis.types.PluginDescriptor;
import com.trinary.rmmv.util.analysis.types.PluginMetaData;
import com.trinary.rmmv.util.analysis.types.PluginMetaDataLocale;
import com.trinary.rmmv.util.types.AmbiguousPluginRO;
import com.trinary.rmmv.util.types.OutOfDatePluginRO;
import com.trinary.rmmv.util.types.ProjectRO;
import com.trinary.rmmv.util.types.UnknownPluginRO;
import com.trinary.rpgmaker.ro.PluginRO;

public class ProjectAnalyzer {
	protected RMMVClientConfig config;
	protected PluginAnalyzer pluginAnalyzer = new PluginAnalyzer();
	protected ObjectMapper mapper = new ObjectMapper();
	
	public ProjectAnalyzer(RMMVClientConfig config) {
		this.config = config;
		mapper.enable(Feature.INDENT_OUTPUT);
	}
	
	public List<ProjectRO> analyzeWorkspace(String rootDirectory) {
		return analyzeWorkspace(rootDirectory, PluginMetaDataLocale.EN);
	}
	
	public List<ProjectRO> analyzeWorkspace(String rootDirectory, PluginMetaDataLocale locale) {
		File dir = new File(rootDirectory);
		
		if (!dir.isDirectory()) {
			return null;
		}
		
		File[] files = dir.listFiles();
		List<ProjectRO> projects = new ArrayList<ProjectRO>();
		
		for (File file : files) {
			if (file.isDirectory()) {
				ProjectRO project = analyzeProject(file.getAbsolutePath(), locale);
				
				if (project != null) {
					projects.add(project);
				}
			}
		}
		
		return projects;
	}
	
	public ProjectRO analyzeProject(String projectDirectory) {
		return analyzeProject(projectDirectory, PluginMetaDataLocale.EN);
	}
	
	public ProjectRO analyzeProject(String projectDirectory, PluginMetaDataLocale locale) {
		File rootDir = new File(projectDirectory);
		
		Map<String, PluginDescriptor> pluginDescriptors = getPluginDescriptors(projectDirectory);
		Map<String, PluginDescriptor> defaultDescriptors = getDefaultPluginDescriptors(projectDirectory, locale);
		List<PluginRO> plugins = getPlugins(projectDirectory);

		ProjectRO project = new ProjectRO();
		project.setName(rootDir.getName());
		project.setPath(rootDir.getAbsolutePath());
		project.setPlugins(plugins);
		project.setPluginDescriptors(pluginDescriptors);
		project.setDefaultDescriptors(defaultDescriptors);
		
		return project;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, PluginDescriptor> getPluginDescriptors(String projectDirectory) {
		File pluginsDescriptorFile = new File(projectDirectory + "/js/plugins.js");
		
		if (!pluginsDescriptorFile.exists() || !pluginsDescriptorFile.isFile()) {
			return null;
		}
		
		Map<String, PluginDescriptor> pluginDescriptors = new HashMap<String, PluginDescriptor>();
		try {
			byte[] encoded = Files.readAllBytes(pluginsDescriptorFile.toPath());
			String pluginDescriptorScript = new String(encoded);
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("nashorn");
			Bindings vars = engine.createBindings();
			engine.eval(pluginDescriptorScript, vars);
			
			Map<String, Object> list = (Map<String, Object>)vars.get("$plugins");
			if (list != null) {
				for (Object element : list.values()) {
					PluginDescriptor descriptor = new PluginDescriptor((Map<String, Object>)element);
					pluginDescriptors.put(descriptor.getName() + ".js", descriptor);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		return pluginDescriptors;
	}
	
	public Map<String, PluginDescriptor> getDefaultPluginDescriptors(String projectDirectory, PluginMetaDataLocale locale) {
		File pluginsDir = new File(projectDirectory + "/js/plugins");
		
		if (!pluginsDir.isDirectory()) {
			return null;
		}
		
		File[] files = pluginsDir.listFiles();
		Map<String, PluginDescriptor> descriptors = new HashMap<String, PluginDescriptor>();
		
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".js")) {
				PluginMetaData metadata = pluginAnalyzer.getPluginMetaData(file, locale);
				descriptors.put(file.getName(), metadata.createPluginDescriptor());
			}
		}
		
		return descriptors;
	}
		
	public List<PluginRO> getPlugins(String projectDirectory) {
		File pluginsDir = new File(projectDirectory + "/js/plugins");
		
		if (!pluginsDir.isDirectory()) {
			return null;
		}
		
		File[] files = pluginsDir.listFiles();
		List<PluginRO> plugins = new ArrayList<PluginRO>();
		
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".js")) {
				PluginRO plugin = null;
				plugin = identifyFile(file);
				
				if (plugin != null) {
					plugins.add(plugin);
				}
			}
		}
		
		return plugins;
	}
	
	public PluginRO identifyFile(File file) {
		PluginVersionClient client = new PluginVersionClient(config);
		
		String hash = getFileHash(file);
		
		List<PluginRO> plugins;
		try {
			plugins = client.getPluginByHash(hash);
		} catch (Exception e) {
			return null;
		}
		
		if (plugins == null || plugins.isEmpty()) {
			UnknownPluginRO unknownPlugin = new UnknownPluginRO();
			unknownPlugin.setFilename(file.getName());
			return unknownPlugin;
		}
		
		if (plugins.size() > 1) {
			AmbiguousPluginRO ambiguousPlugin = new AmbiguousPluginRO();
			ambiguousPlugin.setPlugins(plugins);
			return ambiguousPlugin;
		}
		
		try {
			List<PluginRO> latestVersion = client.getLatestVersion(plugins.get(0));
			if (latestVersion.get(0).getId() != plugins.get(0).getId()) {
				OutOfDatePluginRO outOfDatePlugin = new OutOfDatePluginRO(plugins.get(0));
				outOfDatePlugin.setLatestVersion(latestVersion.get(0));
				outOfDatePlugin.setFilename(file.getName());
				return outOfDatePlugin;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		// Set current plugin to local name
		plugins.get(0).setFilename(file.getName());
		
		return plugins.get(0);
	}
	
	protected String getFileHash(File file) {
        MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}
        FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        
        byte[] dataBytes = new byte[1024];
     
        int nread = 0; 
        try {
			while ((nread = fis.read(dataBytes)) != -1) {
			  md.update(dataBytes, 0, nread);
			}
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		};
        
        return Base64.encodeBase64String(md.digest());
	}
}