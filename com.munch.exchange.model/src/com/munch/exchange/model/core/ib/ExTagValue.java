package com.munch.exchange.model.core.ib;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ib.client.TagValue;

@Entity
public class ExTagValue implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5123539832141701792L;


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	
	private String tag;
	private String value;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OWNER_ID")
	private ExContract owner;
	
	
	public ExTagValue() {
		super();
	}
	
	public ExTagValue(String tag, String value) {
		super();
		this.tag = tag;
		this.value = value;
	}
	
	public ExTagValue(TagValue tagValue) {
		super();
		this.tag = tagValue.m_tag;
		this.value = tagValue.m_value;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		ExTagValue other = (ExTagValue) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	

}
