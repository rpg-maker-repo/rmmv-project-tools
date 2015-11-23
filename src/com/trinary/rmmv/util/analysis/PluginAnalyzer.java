package com.trinary.rmmv.util.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trinary.rmmv.util.analysis.types.PluginMetaData;
import com.trinary.rmmv.util.analysis.types.PluginMetaDataLocale;
import com.trinary.rmmv.util.analysis.types.PluginParam;

public class PluginAnalyzer {
	protected class CommentBlock {
		public PluginMetaDataLocale locale;
		public String comment;
		public Integer nextIndex;
	}
	
	protected CommentBlock getNextCommentBlock(String script, Integer offset) {
		String locale = "EN";
		String sub = script.substring(offset);
		Integer start = sub.indexOf("/*");
		
		Pattern pattern = Pattern.compile("\\/\\*:(.*)");
		Matcher matcher = pattern.matcher(sub);
		
		if (matcher.find()) {
			if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
				locale = matcher.group(1);
			}
		}
		
		if (start < 0) {
			return null;
		}
		
		String comment = sub.substring(start + 2);
		Integer end = comment.indexOf("*/");
		
		if (end < 0) {
			return null;
		}
		
		comment = comment.substring(0, end);
		
		CommentBlock block = new CommentBlock();
		block.comment = comment;
		block.nextIndex = offset + start + end;
		block.locale = PluginMetaDataLocale.valueOf(locale.toUpperCase());
		
		return block;
	}
	
	public PluginMetaData getPluginMetaData(File file, PluginMetaDataLocale locale) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(file.toPath());
			String script = new String(encoded);
			
			PluginMetaData metadata = getPluginMetaData(script, locale);
			metadata.setFilename(file.getName());
			
			return metadata;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PluginMetaData getPluginMetaData(String script, PluginMetaDataLocale locale) {
		PluginMetaData metadata = new PluginMetaData();
		Map<String, String> map = new HashMap<String, String>();
		String lastParamFound = "";
		Integer lastIndex = 0;
		
		CommentBlock commentBlock;
		while((commentBlock = getNextCommentBlock(script, lastIndex)) != null) {
			if (!commentBlock.locale.equals(locale)) {
				lastIndex = commentBlock.nextIndex;
				continue;
			}
			
			Integer end = commentBlock.comment.indexOf("@");
			String buffer = commentBlock.comment.substring(end + 1);
			
			while (end != -1) {
				Integer start = 0;
				end = buffer.indexOf("@");
				
				String markup = "";
				if (end != -1) {
					markup = "@" + buffer.substring(start, end);
				} else {
					markup = "@" + buffer.substring(start);
				}
				
				markup = markup
						.trim()
						.replaceAll("\r\n", "\\$NEWLINE")
						.replaceAll("\n", "\\$NEWLINE")
						.replaceAll("\\*", "")
						.replaceAll("  ", " ");
				
				Pattern pattern = Pattern.compile("@([a-zA-Z0-9]+) (.*)");
				Matcher matcher = pattern.matcher(markup);
				
				if (!matcher.matches()) {
					buffer = buffer.substring(end + 1);
					continue;
				}
				
				String annotation = matcher.group(1).trim();
				String value = matcher.group(2).trim();
				
				if (annotation.equals("help")) {
					value = value.replaceAll("\\$NEWLINE", "\n").trim();
				} else {
					value = value.replaceAll("\\$NEWLINE", "").trim();
				}
				
				if (annotation.equals("param")) {
					metadata.getParameters().put(value, new PluginParam());
					lastParamFound = value;
				} else if (annotation.equals("desc")) {
					metadata.getParameters().get(lastParamFound).setDescription(value);
				} else if (annotation.equals("default")) {
					metadata.getParameters().get(lastParamFound).setDefaultValue(value);
				} else {
					map.put(annotation, value);
				}
				
				buffer = buffer.substring(end + 1);
			}
			lastIndex = commentBlock.nextIndex;
		}
		
		metadata.setPluginName(map.get("pluginname"));
		metadata.setPluginDescription(map.get("plugindesc"));
		metadata.setPluginAuthor(map.get("author"));
		metadata.setPluginVersion(map.get("pluginvers"));
		metadata.setPluginRMVersion(map.get("pluginrmvers"));
		metadata.setHelp(map.get("help"));
		
		return metadata;
	}
}