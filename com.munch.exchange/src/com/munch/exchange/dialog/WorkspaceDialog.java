package com.munch.exchange.dialog;

import java.io.File;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.ResourceManager;

public class WorkspaceDialog extends TitleAreaDialog {

	private LinkedList<String> workspaces=new LinkedList<String>();
	private String workspace;
	private Combo combo;
	private Button button;
	
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public WorkspaceDialog(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
	}
	
	public String getWorkspaces(){
		String r="";
		for(String workspace:workspaces ){
			r+=workspace+";";
		}
		r=r.substring(0, r.lastIndexOf(";"));
		System.out.println("Workspaces: "+r);
		return r;
	}
	
	public void setWorkspaces(String w){
		String[] ws=w.split(";");workspaces.clear();
		for(int i=0;i<ws.length;i++){
			if(i>5)continue;
			File dir=new File(ws[i]);
			if(dir.isDirectory()){
				workspaces.add(ws[i]);
			}
		}
		
	}
	
	public String getLastWorkspace(){
		if(workspaces.isEmpty())return null;
		return workspaces.getFirst();
	}
	
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Exchange have to save data to a local directory");
		setTitleImage(ResourceManager.getPluginImage("com.munch.exchange", "icons/login_dialog.gif"));
		setTitle("Select a workspace");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridLayout gl_container = new GridLayout(3, false);
		gl_container.marginHeight = 15;
		container.setLayout(gl_container);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblWorkspace = new Label(container, SWT.NONE);
		lblWorkspace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWorkspace.setText("Workspace:");
		
		combo = new Combo(container, SWT.NONE);
		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(button==null)return;
				File dir=new File(combo.getText());
				button.setEnabled(dir.isDirectory());
				if(workspaces.contains(combo.getText())){
					workspaces.remove(combo.getText());
					workspaces.addFirst(combo.getText());
				}
				
				
			}
		});
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		int i=0;
		for(String w_s:this.workspaces){
			combo.add(w_s);
			if(w_s.equals(workspace)){
				combo.select(i);
			}
			i++;
		}
		
		
		Button btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				DirectoryDialog dialog= new DirectoryDialog(getShell(),SWT.OPEN );
				if(getLastWorkspace()!=null){
					dialog.setFilterPath(getLastWorkspace());
				}
				//dialog.
				String path=dialog.open();
				if(path!=null && !path.isEmpty()){
					
					File dir=new File(path);
					if(dir.isDirectory()){
						combo.add(path, 0);
						combo.select(0);
						if(!workspaces.contains(path))
							workspaces.addFirst(path);
					}
				}
				
			}
		});
		btnBrowse.setText("Browse...");
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		//button.setEnabled(false);
		
		File dir=new File(combo.getText());
		button.setEnabled(dir.isDirectory());
		
		
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(706, 307);
	}

}
