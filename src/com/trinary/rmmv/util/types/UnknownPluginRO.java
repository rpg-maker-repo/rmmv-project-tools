package com.trinary.rmmv.util.types;

import com.trinary.rpgmaker.ro.PluginRO;

public class UnknownPluginRO extends PluginRO {
	public String filename;

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
}