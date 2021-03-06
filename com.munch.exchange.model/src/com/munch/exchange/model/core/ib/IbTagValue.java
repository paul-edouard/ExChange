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
public class IbTagValue implements Serializable,Copyable<IbTagValue> {
	
	
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
	private IbContract owner;
	
	
	public IbTagValue() {
		super();
	}
	
	public IbTagValue(String tag, String value) {
		super();
		this.tag = tag;
		this.value = value;
	}
	
	public IbTagValue(TagValue tagValue) {
		super();
		this.tag = tagValue.m_tag;
		this.value = tagValue.m_value;
	}
	
	@Override
	public IbTagValue copy() {
		IbTagValue c=new IbTagValue();
		
		c.id=this.id;
		c.tag=this.tag;
		c.value=this.value;
		//c.owner=this.owner;
		
		return null;
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
		IbTagValue other = (IbTagValue) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExTagValue [id=" + id + ", tag=" + tag + ", value=" + value
				+ "]";
	}

	public IbContract getOwner() {
		return owner;
	}

	public void setOwner(IbContract owner) {
		this.owner = owner;
	}

	
	
	
	

}
