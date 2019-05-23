/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.si;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.ConstUI;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class LibPhonenumberPDIPluginDialog extends BaseStepDialog implements StepDialogInterface {

  private static Class<?> PKG = LibPhonenumberPDIPluginMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

  private static final int MARGIN_SIZE = 15;
  private static final int LABEL_SPACING = 5;
  private static final int ELEMENT_SPACING = 10;

  private static final int LARGE_FIELD = 350;
  private static final int MEDIUM_FIELD = 250;
  private static final int SMALL_FIELD = 75;

  private LibPhonenumberPDIPluginMeta meta;

  private Label wlStepname;
  private Text wStepname;
  private FormData fdStepname, fdlStepname;

  private Label lfname;
  private CCombo wInFieldCombo;
  private FormData fdlFname, fdStep;

  private Label wRegionFieldName;
  private TextVar wRegionField;
  private FormData fdlRegionFieldName, fdlRegionField;

  private Label wOutFieldName;
  private TextVar wOutField;
  private FormData fdlOutFieldName, fdlOutField;

  private Label wCountryCodeFieldName;
  private TextVar wCountryCodeField;
  private FormData fdlCountryCodeFieldName, fdlCountryCodeField;


  private Label wCheckValidName;
  private Button wcheckValid;
  private FormData fdlCheckValid, fdlCheckValidName;

  private Label wFindMatchesName;
  private Button wFindMatches;
  private FormData fdlFindMatches, fdlFindMatchesName;


  private Button wCancel;
  private Button wOK;
  private ModifyListener lsMod;
  private Listener lsCancel;
  private Listener lsOK;
  private SelectionAdapter lsDef;
  private boolean changed;

  public LibPhonenumberPDIPluginDialog( Shell parent, Object in, TransMeta tr, String sname ) {
    super( parent, (BaseStepMeta) in, tr, sname );
    meta = (LibPhonenumberPDIPluginMeta) in;
  }

  public String open() {
    // store some convenient SWT variables
    Shell parent = getParent();
    Display display = parent.getDisplay();

    // SWT code for preparing the dialog
    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
    props.setLook(shell);
    setShellImage(shell, meta);

    // Save the value of the changed flag on the meta object. If the user cancels
    // the dialog, it will be restored to this saved value.
    // The "changed" variable is inherited from BaseStepDialog
    changed = meta.hasChanged();

    // The ModifyListener used on all controls. It will update the meta object to
    // indicate that changes are being made.
    ModifyListener lsMod = new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        meta.setChanged();
      }
    };

    // ------------------------------------------------------- //
    // SWT code for building the actual settings dialog        //
    // ------------------------------------------------------- //
    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;
    shell.setLayout(formLayout);
    shell.setText(BaseMessages.getString(PKG, "LibPhonenumberPDIPluginDialog.Shell.Title"));
    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Stepname line
    wlStepname = new Label(shell, SWT.RIGHT);
    wlStepname.setText(BaseMessages.getString(PKG, "LibPhonenumberPDIPluginDialog.Stepname.Label"));
    props.setLook(wlStepname);
    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment(0, 0);
    fdlStepname.right = new FormAttachment(middle, -margin);
    fdlStepname.top = new FormAttachment(0, margin);
    wlStepname.setLayoutData(fdlStepname);

    wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wStepname.setText(stepname);
    props.setLook(wStepname);
    wStepname.addModifyListener(lsMod);
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment(middle, 0);
    fdStepname.top = new FormAttachment(0, margin);
    fdStepname.right = new FormAttachment(100, 0);
    wStepname.setLayoutData(fdStepname);

    // Set the in field name
    lfname = new Label( shell, SWT.RIGHT );
    lfname.setText( BaseMessages.getString( PKG, "LibPhonenumberPDIPluginDialog.Fields.FieldName" ) );
    props.setLook( lfname );
    fdlFname = new FormData();
    fdlFname.left = new FormAttachment( 0, 0 );
    fdlFname.right = new FormAttachment( middle, -margin );
    fdlFname.top = new FormAttachment( wStepname, 15 );
    lfname.setLayoutData( fdlFname );

    wInFieldCombo = new CCombo( shell, SWT.BORDER );
    props.setLook( wInFieldCombo );
    StepMeta stepinfo = transMeta.findStep( stepname );
    if ( stepinfo != null ) {
      try {
        String[] fields = transMeta.getStepFields(stepname).getFieldNames();
        for (int i = 0; i < fields.length; i++) {
          wInFieldCombo.add(fields[i]);
        }
      }catch(KettleException e){
        if ( log.isBasic())
          logBasic("Failed to Get Step Fields");
      }
    }

    wInFieldCombo.addModifyListener( lsMod );
    fdStep = new FormData();
    fdStep.left = new FormAttachment( middle, 0 );
    fdStep.top = new FormAttachment( wStepname, 15 );
    fdStep.right = new FormAttachment( 100, 0 );
    wInFieldCombo.setLayoutData( fdStep );

    //set the region field
    wRegionFieldName = new Label(shell, SWT.RIGHT);
    wRegionFieldName.setText(BaseMessages.getString(PKG, "LibPhonenumberPDIPluginDialog.Output.Region"));
    props.setLook(wRegionFieldName);
    fdlRegionFieldName = new FormData();
    fdlRegionFieldName.left = new FormAttachment(0, 0);
    fdlRegionFieldName.top = new FormAttachment(lfname, 15);
    fdlRegionFieldName.right = new FormAttachment(middle, -margin);
    wRegionFieldName.setLayoutData(fdlRegionFieldName);
    wRegionField = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wRegionField.setText("");
    wRegionField.addModifyListener(lsMod);
    props.setLook(wRegionField);
    fdlRegionField = new FormData();
    fdlRegionField.left = new FormAttachment(middle, 0);
    fdlRegionField.top = new FormAttachment(lfname, 15);
    fdlRegionField.right = new FormAttachment(100, 0);
    wRegionField.setLayoutData(fdlRegionField);

    //outfield
    wOutFieldName = new Label(shell, SWT.RIGHT);
    wOutFieldName.setText(BaseMessages.getString(PKG, "LibPhonenumberPDIPluginDialog.Output.FieldName"));
    props.setLook(wOutFieldName);
    fdlOutFieldName = new FormData();
    fdlOutFieldName.left = new FormAttachment(0, 0);
    fdlOutFieldName.top = new FormAttachment(wRegionFieldName, 15);
    fdlOutFieldName.right = new FormAttachment(middle, -margin);
    wOutFieldName.setLayoutData(fdlOutFieldName);
    wOutField = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wOutField.setText("");
    wOutField.addModifyListener(lsMod);
    props.setLook(wOutField);
    fdlOutField = new FormData();
    fdlOutField.left = new FormAttachment(middle, 0);
    fdlOutField.top = new FormAttachment(wRegionFieldName, 15);
    fdlOutField.right = new FormAttachment(100, 0);
    wOutField.setLayoutData(fdlOutField);

    //country code
    wCountryCodeFieldName = new Label(shell, SWT.RIGHT);
    wCountryCodeFieldName.setText(BaseMessages.getString(PKG, "LibPhonenumberPDIPluginDialog.Output.CountryCode"));
    props.setLook(wCountryCodeFieldName);
    fdlCountryCodeFieldName = new FormData();
    fdlCountryCodeFieldName.left = new FormAttachment(0, 0);
    fdlCountryCodeFieldName.top = new FormAttachment(wOutFieldName, 15);
    fdlCountryCodeFieldName.right = new FormAttachment(middle, -margin);
    wCountryCodeFieldName.setLayoutData(fdlCountryCodeFieldName);
    wCountryCodeField = new TextVar(transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
    wCountryCodeField.setText("");
    wCountryCodeField.addModifyListener(lsMod);
    props.setLook(wCountryCodeField);
    fdlCountryCodeField = new FormData();
    fdlCountryCodeField.left = new FormAttachment(middle, 0);
    fdlCountryCodeField.top = new FormAttachment(wOutFieldName, 15);
    fdlCountryCodeField.right = new FormAttachment(100, 0);
    wCountryCodeField.setLayoutData(fdlCountryCodeField);

    //find matches
    wFindMatchesName = new Label(shell,SWT.RIGHT);
    wFindMatchesName.setText(BaseMessages.getString(PKG,"LibPhonenumberPDIPluginDialog.Output.FindMatch"));
    props.setLook(wFindMatchesName);
    fdlFindMatchesName = new FormData();
    fdlFindMatchesName.left = new FormAttachment(0, 0);
    fdlFindMatchesName.top = new FormAttachment(wCountryCodeFieldName, 15);
    fdlFindMatchesName.right = new FormAttachment(middle, -margin);
    wFindMatchesName.setLayoutData(fdlFindMatchesName);
    wFindMatches = new Button(shell, SWT.CHECK);
    props.setLook(wFindMatches);
    fdlFindMatches = new FormData();
    fdlFindMatches.left = new FormAttachment(middle, 0);
    fdlFindMatches.top = new FormAttachment(wCountryCodeFieldName, 15);
    fdlFindMatches.right = new FormAttachment(100, 0);
    wFindMatches.setLayoutData(fdlFindMatches);

    //flag for checkvalid
    wCheckValidName = new Label(shell,SWT.RIGHT);
    wCheckValidName.setText(BaseMessages.getString(PKG,"LibPhonenumberPDIPluginDialog.Output.CheckValid"));
    props.setLook(wCheckValidName);
    fdlCheckValidName = new FormData();
    fdlCheckValidName.left = new FormAttachment(0, 0);
    fdlCheckValidName.top = new FormAttachment(wFindMatchesName, 15);
    fdlCheckValidName.right = new FormAttachment(middle, -margin);
    wCheckValidName.setLayoutData(fdlCheckValidName);
    wcheckValid = new Button(shell, SWT.CHECK);
    props.setLook(wcheckValid);
    fdlCheckValid = new FormData();
    fdlCheckValid.left = new FormAttachment(middle, 0);
    fdlCheckValid.top = new FormAttachment(wFindMatchesName, 15);
    fdlCheckValid.right = new FormAttachment(100, 0);
    wcheckValid.setLayoutData(fdlCheckValid);

    // OK and cancel buttons
    wOK = new Button(shell, SWT.PUSH);
    wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
    wCancel = new Button(shell, SWT.PUSH);
    wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));
    setButtonPositions(new Button[]{wOK, wCancel}, margin, wCheckValidName);

    // Add listeners for cancel and OK
    lsCancel = new Listener() {
      public void handleEvent(Event e) {
        cancel();
      }
    };
    lsOK = new Listener() {
      public void handleEvent(Event e) {
        ok();
      }
    };
    wCancel.addListener(SWT.Selection, lsCancel);
    wOK.addListener(SWT.Selection, lsOK);

    // default listener (for hitting "enter")
    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent e) {
        ok();
      }
    };
    wStepname.addSelectionListener(lsDef);
    wcheckValid.addSelectionListener(lsDef);
    wFindMatches.addSelectionListener(lsDef);
    wCountryCodeField.addSelectionListener(lsDef);
    wOutField.addSelectionListener(lsDef);
    wInFieldCombo.addSelectionListener(lsDef);
    wRegionField.addSelectionListener(lsDef);


    // Detect X or ALT-F4 or something that kills this window and cancel the dialog properly
    shell.addShellListener(new ShellAdapter() {
      public void shellClosed(ShellEvent e) {
        cancel();
      }
    });

    // Set/Restore the dialog size based on last position on screen
    // The setSize() method is inherited from BaseStepDialog
    setSize();

    // populate the dialog with the values from the meta object
    getData();

    // restore the changed flag to original value, as the modify listeners fire during dialog population
    meta.setChanged(changed);

    // open dialog and enter event loop
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }

    // at this point the dialog has closed, so either ok() or cancel() have been executed
    // The "stepname" variable is inherited from BaseStepDialog
    return stepname;
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    wStepname.selectAll();
    wInFieldCombo.setText(Const.NVL(meta.getInField(), ""));
    wOutField.setText(Const.NVL(meta.getOutField(), ""));
    wCountryCodeField.setText(Const.NVL(meta.getCountryCodeField(), ""));
    wcheckValid.setSelection(meta.isCheckValid());
    wFindMatches.setSelection(meta.isFindMatches());
    wRegionField.setText(Const.NVL(meta.getRegion(), ""));
    wStepname.setFocus();
  }

  private Image getImage() {
    PluginInterface plugin =
        PluginRegistry.getInstance().getPlugin( StepPluginType.class, stepMeta.getStepMetaInterface() );
    String id = plugin.getIds()[0];
    if ( id != null ) {
      return GUIResource.getInstance().getImagesSteps().get( id ).getAsBitmapForSize( shell.getDisplay(),
          ConstUI.ICON_SIZE, ConstUI.ICON_SIZE );
    }
    return null;
  }

  private void cancel() {
    dispose();
  }

  private void ok() {
    stepname = wStepname.getText();
    String inField = wInFieldCombo.getText();
    String outField = wOutField.getText();
    String ccode = wCountryCodeField.getText();
    String region = wRegionField.getText();
    boolean checkValid = wcheckValid.getSelection();
    boolean findMatches = wFindMatches.getSelection();

    meta.setInField(inField);
    meta.setOutField(outField);
    meta.setCountryCodeField(ccode);
    meta.setFindMatches(findMatches);
    meta.setCheckValid(checkValid);
    meta.setRegion(region);
    dispose();
  }
}