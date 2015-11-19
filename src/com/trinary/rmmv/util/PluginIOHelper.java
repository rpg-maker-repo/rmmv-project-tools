package com.trinary.rmmv.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.trinary.rmmv.client.PluginClient;
import com.trinary.rmmv.client.PluginVersionClient;
import com.trinary.rmmv.client.RMMVClientConfig;
import com.trinary.rpgmaker.ro.PluginRO;

public class PluginIOHelper {
	protected PluginClient pluginClient;
	protected PluginVersionClient versionClient;
	
	public PluginIOHelper(RMMVClientConfig config) {
		versionClient = new PluginVersionClient(config);
		pluginClient = new PluginClient(config);
	}
	
	public void deletePlugin(PluginRO plugin, String location) {
		System.out.println("Deleting    " + plugin.getFilename() + " (" + plugin.getVersion() + ")");
		
		File file = new File(location + plugin.getFilename());
		file.delete();
	}
	
	public void storePlugin(PluginRO plugin, String location) {
		System.out.println("Downloading " + plugin.getFilename() + " (" + plugin.getVersion() + ")");
		
		String script = null;
		try {
			script = versionClient.getScript(plugin);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
	
		try {
			FileWriter writer = new FileWriter(location + plugin.getFilename());
			writer.write(script);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
	}
	
	public void storeDependencies(PluginRO plugin, String location) {
		List<PluginRO> dependencies;
		try {
			dependencies = versionClient.getDependencies(plugin);
		} catch (Exception e2) {
			e2.printStackTrace();
			return;
		}
		
		for (PluginRO dependency : dependencies) {
			storePlugin(dependency, location);
			storeDependencies(dependency, location);
		}
	}
}