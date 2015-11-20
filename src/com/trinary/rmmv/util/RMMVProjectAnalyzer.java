package com.trinary.rmmv.util;

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

import com.trinary.rmmv.client.PluginVersionClient;
import com.trinary.rmmv.client.RMMVClientConfig;
import com.trinary.rmmv.util.types.AmbiguousPluginRO;
import com.trinary.rmmv.util.types.OutOfDatePluginRO;
import com.trinary.rmmv.util.types.PluginDescriptor;
import com.trinary.rmmv.util.types.ProjectRO;
import com.trinary.rmmv.util.types.UnknownPluginRO;
import com.trinary.rpgmaker.ro.PluginRO;

public class RMMVProjectAnalyzer {
	protected RMMVClientConfig config;
	
	public RMMVProjectAnalyzer(RMMVClientConfig config) {
		this.config = config;
	}
	
	public List<ProjectRO> analyzeWorkspace(String rootDirectory) {
		File dir = new File(rootDirectory);
		
		if (!dir.isDirectory()) {
			return null;
		}
		
		File[] files = dir.listFiles();
		List<ProjectRO> projects = new ArrayList<ProjectRO>();
		
		for (File file : files) {
			if (file.isDirectory()) {
				ProjectRO project = analyzeProject(file.getAbsolutePath());
				
				if (project != null) {
					projects.add(project);
				}
			}
		}
		
		return projects;
	}
	
	@SuppressWarnings("unchecked")
	public ProjectRO analyzeProject(String projectDirectory) {
		File rootDir = new File(projectDirectory);
		File pluginsDescriptorFile = new File(projectDirectory + "/js/plugins.js");
		File pluginsDir = new File(projectDirectory + "/js/plugins");
		
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
					System.out.println("DESCRIPTOR: " + descriptor);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
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
		
		ProjectRO project = new ProjectRO();
		project.setName(rootDir.getName());
		project.setPath(rootDir.getAbsolutePath());
		project.setPlugins(plugins);
		project.setPluginDescriptors(pluginDescriptors);
		
		return project;
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