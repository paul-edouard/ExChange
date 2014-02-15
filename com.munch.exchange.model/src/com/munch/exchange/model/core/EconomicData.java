package com.munch.exchange.model.core;

import java.util.Calendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.XmlParameterElement;

public class EconomicData extends ExchangeRate {

	
	static final String FIELD_Frequency="frequency";
	static final String FIELD_FrequencyShort="frequencyShort";
	
	static final String FIELD_Units="units";
	static final String FIELD_UnitsShort="unitsShort";
	
	static final String FIELD_SeasonalAdjustment="seasonalAdjustment";
	static final String FIELD_SeasonalAdjustmentShort="seasonalAdjustmentShort";
	
	static final String FIELD_LastUpdated="lastUpdated";
	static final String FIELD_Popularity="popularity";
	static final String FIELD_Notes="notes";
	
	
	private String frequency;
	private String frequencyShort;
	
	private String units;
	private String unitsShort;
	
	private String seasonalAdjustment;
	private String seasonalAdjustmentShort;
	
	private Calendar lastUpdated;
	private String popularity;
	private String notes;
	
	
	public EconomicData() {
		super();
		this.stockExchange="St. Louis FED";
	}


	public String getFrequency() {
		return frequency;
	}


	public void setFrequency(String frequency) {
	changes.firePropertyChange(FIELD_Frequency, this.frequency, this.frequency = frequency);}
	


	public String getFrequencyShort() {
		return frequencyShort;
	}


	public void setFrequencyShort(String frequencyShort) {
	changes.firePropertyChange(FIELD_FrequencyShort, this.frequencyShort, this.frequencyShort = frequencyShort);}
	


	public String getUnits() {
		return units;
	}


	public void setUnits(String units) {
	changes.firePropertyChange(FIELD_Units, this.units, this.units = units);}
	


	public String getUnitsShort() {
		return unitsShort;
	}


	public void setUnitsShort(String unitsShort) {
	changes.firePropertyChange(FIELD_UnitsShort, this.unitsShort, this.unitsShort = unitsShort);}
	


	public String getSeasonalAdjustment() {
		return seasonalAdjustment;
	}


	public void setSeasonalAdjustment(String seasonalAdjustment) {
	changes.firePropertyChange(FIELD_SeasonalAdjustment, this.seasonalAdjustment, this.seasonalAdjustment = seasonalAdjustment);}
	


	public String getSeasonalAdjustmentShort() {
		return seasonalAdjustmentShort;
	}


	public void setSeasonalAdjustmentShort(String seasonalAdjustmentShort) {
	changes.firePropertyChange(FIELD_SeasonalAdjustmentShort, this.seasonalAdjustmentShort, this.seasonalAdjustmentShort = seasonalAdjustmentShort);}
	


	public Calendar getLastUpdated() {
		return lastUpdated;
	}


	public void setLastUpdated(Calendar lastUpdated) {
	changes.firePropertyChange(FIELD_LastUpdated, this.lastUpdated, this.lastUpdated = lastUpdated);}
	


	public String getPopularity() {
		return popularity;
	}


	public void setPopularity(String popularity) {
	changes.firePropertyChange(FIELD_Popularity, this.popularity, this.popularity = popularity);}
	


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
	changes.firePropertyChange(FIELD_Notes, this.notes, this.notes = notes);}

	
	
	


	@Override
	public String toString() {
		return "EconomicData [frequency=" + frequency + ", frequencyShort="
				+ frequencyShort + ", units=" + units + ", unitsShort="
				+ unitsShort + ", seasonalAdjustment=" + seasonalAdjustment
				+ ", seasonalAdjustmentShort=" + seasonalAdjustmentShort
				+ ", lastUpdated=" + lastUpdated + ", popularity=" + popularity
				+ ", notes=" + notes + ", start=" + start + ", end=" + end
				+ ", name=" + name + ", symbol=" + symbol + ", dataPath="
				+ dataPath + ", stockExchange=" + stockExchange
				+ ", historicalData=" + historicalData + ", recordedQuote="
				+ recordedQuote + ", uuid=" + uuid + ", changes=" + changes
				+ ", parameter=" + parameter + "]";
	}


	/***********************************
	 *                                 *
	 *		       XML                 *
	 *                                 *
	 ***********************************/
	
	@Override
	protected void initAttribute(Element rootElement) {
		
		
		this.setFrequency(rootElement.getAttribute(FIELD_Frequency));
		this.setFrequencyShort(rootElement.getAttribute(FIELD_FrequencyShort));
		
		this.setUnits(rootElement.getAttribute(FIELD_Units));
		this.setUnitsShort(rootElement.getAttribute(FIELD_UnitsShort));
		
		this.setSeasonalAdjustment(rootElement.getAttribute(FIELD_SeasonalAdjustment));
		this.setSeasonalAdjustmentShort(rootElement.getAttribute(FIELD_SeasonalAdjustmentShort));
		
		this.setLastUpdated(DateTool.StringToDate(rootElement.getAttribute(FIELD_LastUpdated)));
		this.setPopularity(rootElement.getAttribute(FIELD_Popularity));
		this.setNotes(rootElement.getAttribute(FIELD_Notes));
		
		
		
		super.initAttribute(rootElement);
	}


	@Override
	protected void setAttribute(Element rootElement) {
		
		rootElement.setAttribute(FIELD_Frequency, this.getFrequency());
		rootElement.setAttribute(FIELD_FrequencyShort, this.getFrequencyShort());
		
		rootElement.setAttribute(FIELD_Units, this.getUnits());
		rootElement.setAttribute(FIELD_UnitsShort, this.getUnitsShort());
		
		rootElement.setAttribute(FIELD_SeasonalAdjustment, this.getSeasonalAdjustment());
		rootElement.setAttribute(FIELD_SeasonalAdjustmentShort, this.getSeasonalAdjustmentShort());
		
		
		rootElement.setAttribute(FIELD_LastUpdated,DateTool.dateToString( this.getLastUpdated()));
		rootElement.setAttribute(FIELD_Popularity, this.getPopularity());
		rootElement.setAttribute(FIELD_Notes, this.getNotes());
		
		super.setAttribute(rootElement);
	}
	
	
	public class EconomicDataCategory extends XmlParameterElement{
		
		static final String FIELD_Id="id";
		static final String FIELD_ParentId="parentId";
		static final String FIELD_Name="name";
		static final String FIELD_Parent="parent";
		
		private String id;
		private String parentId;
		private String name;
		private EconomicDataCategory parent;
		
		
		

		private String getId() {
			return id;
		}

		private void setId(String id) {
		changes.firePropertyChange(FIELD_Id, this.id, this.id = id);}
		

		private String getParentId() {
			return parentId;
		}

		private void setParentId(String parentId) {
		changes.firePropertyChange(FIELD_ParentId, this.parentId, this.parentId = parentId);}
		

		private String getName() {
			return name;
		}

		private void setName(String name) {
		changes.firePropertyChange(FIELD_Name, this.name, this.name = name);}
		

		private EconomicDataCategory getParent() {
			return parent;
		}

		private void setParent(EconomicDataCategory parent) {
		changes.firePropertyChange(FIELD_Parent, this.parent, this.parent = parent);}
		

		@Override
		protected void initAttribute(Element rootElement) {
			this.setId(rootElement.getAttribute(FIELD_Id));
			this.setParentId(rootElement.getAttribute(FIELD_ParentId));
			this.setName(rootElement.getAttribute(FIELD_Name));
			
		}

		@Override
		protected void initChild(Element childElement) {
			if(childElement.getTagName().equals(this.getTagName())){
				EconomicDataCategory cat=new EconomicDataCategory();
				cat.init(childElement);
				this.setParent(cat);
			}
			
		}

		@Override
		protected void setAttribute(Element rootElement) {
			rootElement.setAttribute(FIELD_Id, this.getId());
			rootElement.setAttribute(FIELD_ParentId, this.getParentId());
			rootElement.setAttribute(FIELD_Name, this.getName());
			
		}

		@Override
		protected void appendChild(Element rootElement, Document doc) {
			//Parameter
			rootElement.appendChild(this.getParent().toDomElement(doc));
			
		}

		@Override
		public String toString() {
			return "EconomicDataCategory [id=" + id + ", parentId=" + parentId
					+ ", name=" + name + ", parent=" + parent + "]";
		}
		
		
		
		
	}
	
	
	
	

}
