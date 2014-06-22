package com.munch.exchange.parts.chart;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;



public class IndicatorParameter {
	
	private IndicatorComposite parent;
	
	private Label valueLabel;
	private Slider slider;
	
	private Type type;
	private String name;
	private double value;
	private double minValue;
	private double maxValue;
	private int scalarFactor;
	
	public enum Type { DOUBLE, INTEGER, NONE};
	
	public IndicatorParameter(String name,Type type,  double val, double minValue, double maxValue, int  scalarFac, IndicatorComposite par){
		this.value=val;
		this.maxValue=maxValue;
		this.minValue=minValue;
		this.type=type;
		this.name=name;
		
		this.scalarFactor=scalarFac;
		
		this.parent=par;
		
		Label lblAlpha = new Label(this.parent, SWT.NONE);
		lblAlpha.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblAlpha.setText(name+":");
		
		valueLabel = new Label(this.parent, SWT.NONE);
		valueLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		valueLabel.setText(getStringValue());
		
		slider = new Slider(this.parent, SWT.NONE);
		slider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider.setMaximum((int) (this.maxValue*Math.pow(10, this.scalarFactor)));
		slider.setMinimum((int) (this.minValue*Math.pow(10, this.scalarFactor)));
		slider.setPageIncrement(1);
		//slider.setIncrement(1);
		slider.setThumb(1);
		slider.setSelection((int) (this.value*Math.pow(10, this.scalarFactor)));
		slider.setEnabled(false);
		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				value= (((double)slider.getSelection())/Math.pow(10, scalarFactor));
				valueLabel.setText(getStringValue());
				if(parent.getActivatorBtn().isEnabled())
					parent.fireCollectionRemoved();
				
			}
		});
		
	}
	
	public int getIntegerValue() {
		return (int) value;
	}
	
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		if(value>=minValue && value<=maxValue){
			this.value = value;
			refresh();
		}
	}

	public String getName() {
		return name;
	}

	public Slider getSlider() {
		return slider;
	}
	
	private void refresh(){
		valueLabel.setText(getStringValue());
		slider.setSelection((int) (this.value*Math.pow(10, this.scalarFactor)));
	}
	
	private String getStringValue(){
		if(value==0){
			return "00000";
		}
		
		if(type==Type.INTEGER){
			String val_str=String.valueOf((int) value);
			String max_str=String.valueOf((int) maxValue);
			while(val_str.length()<=max_str.length())
				val_str="_"+val_str;
			return val_str;
		}
		else if(type==Type.DOUBLE){
			String reg="%,."+String.valueOf(scalarFactor)+"f%%";
			return String.format(reg,  value);
		}
		return "";
	}


	
	
	

}
