package com.munch.exchange.parts.chart.parameter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import com.munch.exchange.IEventConstant;
import com.munch.exchange.model.core.chart.ChartParameter;
import com.munch.exchange.model.core.ib.chart.IbChartParameter;

public class IbChartParameterComposite {
	
	private ChartParameterEditorPart parent;
	private IbChartParameter parameter;
	
	private Text valueLabel;
	private Slider slider;
	private Label lblAlpha;
	
	
	public IbChartParameterComposite(ChartParameterEditorPart p,Composite parentComposite, IbChartParameter param) {
		//super(parentComposite, SWT.NONE);
		
		this.parent=p;
		this.parameter=param;
		
		//this.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//this.setLayout(new GridLayout(3, false));
		
		
		lblAlpha = new Label(parentComposite, SWT.NONE);
		lblAlpha.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblAlpha.setText(this.parameter.getName()+":");
		
		valueLabel = new Text(parentComposite, SWT.NONE);
		valueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		valueLabel.setText(getStringValue());
		valueLabel.setEditable(false);
		
		slider = new Slider(parentComposite, SWT.NONE);
		slider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider.setMaximum((int) (this.parameter.getMaxValue()*Math.pow(10, this.parameter.getScalarFactor())));
		slider.setMinimum((int) (this.parameter.getMinValue()*Math.pow(10, this.parameter.getScalarFactor())));
		slider.setPageIncrement(1);
		//slider.setIncrement(1);
		slider.setThumb(1);
		slider.setSelection((int) (this.parameter.getValue()*Math.pow(10, this.parameter.getScalarFactor())));
		slider.setEnabled(false);
		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parameter.setValue((((double)slider.getSelection())/Math.pow(10, parameter.getScalarFactor())));
				valueLabel.setText(getStringValue());
				//parameter.getIndicator().fireParametersChanged();
				parent.getEventBroker().post(IEventConstant.IB_CHART_INDICATOR_PARAMETER_CHANGED, parameter.getIndicator());
				
			}
		});
		
		
	}
	
	public void dispose(){
		lblAlpha.dispose();
		valueLabel.dispose();
		slider.dispose();
	}
	
	
	public int getIntegerValue() {
		return (int) parameter.getValue();
	}
	
	public double getValue() {
		return parameter.getValue();
	}
	
	public void setValue(double value) {
		if(value>=parameter.getMinValue() && value<=parameter.getMaxValue()){
			parameter.setValue(value);
			refresh();
			//System.out.println("Set value: "+parameter.getIndicator());
		}
	}
	
	public String getName() {
		return parameter.getName();
	}
	
	public Slider getSlider() {
		return slider;
	}
	
	public void refresh(){
		//System.out.println("Set value");
		valueLabel.setText(getStringValue());
		slider.setSelection((int) (parameter.getValue()*Math.pow(10, parameter.getScalarFactor())));
	}
	
	private String getStringValue(){
		if(parameter.getValue()==0){
			return "00000";
		}
		
		switch (parameter.getType()) {
		case INTEGER:
			String val_str=String.valueOf((int) parameter.getValue());
			//String max_str=String.valueOf((int) parameter.getMaxValue());
			//while(val_str.length()<=max_str.length())
			//	val_str="_"+val_str;
			return val_str;
		case DOUBLE:
			String reg="%,."+String.valueOf(parameter.getScalarFactor())+"f%%";
			return String.format(reg,  parameter.getValue());
		default:
			return "";
		}
		
		
	}
	
	
	

}
