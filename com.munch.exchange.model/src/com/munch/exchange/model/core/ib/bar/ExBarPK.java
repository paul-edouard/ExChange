package com.munch.exchange.model.core.ib.bar;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.ib.controller.Types.WhatToShow;

@Embeddable
public class ExBarPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7976841729771418153L;
	
	
	private long time;
	
	private WhatToShow type;
	

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public WhatToShow getType() {
		return type;
	}

	public void setType(WhatToShow type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (time ^ (time >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExBarPK other = (ExBarPK) obj;
		if (time != other.time)
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	
	
	

}
