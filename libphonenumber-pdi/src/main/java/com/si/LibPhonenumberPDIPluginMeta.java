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

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import java.util.List;


/**
 * Skeleton for PDI Step plugin.
 */
@Step( id = "LibPhonenumberPDIPlugin", image = "LibPhonenumberPDIPlugin.svg", name = "ETL Phone Numbers",
    description = "Extract and standardize phone numbers.", categoryDescription = "Transform" )
public class LibPhonenumberPDIPluginMeta extends BaseStepMeta implements StepMetaInterface {
  private String region;
  private String inField;
  private String outField;
  private String countryCodeField;
  private boolean checkValid;
  private boolean findMatches;

  private static Class<?> PKG = LibPhonenumberPDIPlugin.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

  public LibPhonenumberPDIPluginMeta() {
    super();
  }

  public String getCountryCodeField() {
    return countryCodeField;
  }

  public void setCountryCodeField(String countryCodeField) {
    this.countryCodeField = countryCodeField;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getInField() {
    return inField;
  }

  public void setInField(String inField) {
    this.inField = inField;
  }

  public String getOutField() {
    return outField;
  }

  public void setOutField(String outField) {
    this.outField = outField;
  }

  public boolean isCheckValid() {
    return checkValid;
  }

  public void setCheckValid(boolean checkValid) {
    this.checkValid = checkValid;
  }

  public boolean isFindMatches() {
    return findMatches;
  }

  public void setFindMatches(boolean findMatches) {
    this.findMatches = findMatches;
  }

  public String getXML() throws KettleValueException {
    StringBuilder xml = new StringBuilder();
    xml.append( XMLHandler.addTagValue( "inField", inField ) );
    xml.append(XMLHandler.addTagValue("outField", outField));
    xml.append(XMLHandler.addTagValue("countryCodeField", countryCodeField));
    xml.append(XMLHandler.addTagValue("checkValid", checkValid));
    xml.append(XMLHandler.addTagValue("findMatches", findMatches));
    xml.append(XMLHandler.addTagValue("region", region));
    return xml.toString();
  }

  public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
    readData( stepnode );
  }

  public Object clone() {
    Object retval = super.clone();
    return retval;
  }
  
  private void readData( Node stepnode ) throws KettleXMLException {
    try {
      setInField(Const.NVL(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "inField")), ""));
      setOutField(Const.NVL(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "outField")), ""));
      setCountryCodeField(Const.NVL(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "countryCodeField")), ""));
      setRegion(Const.NVL(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "region")), ""));
      setCheckValid(Const.NVL(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "checkValid")), "N").equals("Y"));
      setFindMatches(Const.NVL(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "findMatches")), "N").equals("Y"));
    } catch ( Exception e ) {
      throw new KettleXMLException( "Demo plugin unable to read step info from XML node", e );
    }
  }

  public void setDefault() {
    inField = "";
    outField = "";
    countryCodeField = "";
    region = "";
    checkValid = false;
    findMatches = false;
  }

  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases ) throws KettleException {
    try {
      inField  = rep.getStepAttributeString(id_step, "inField" );
      outField = rep.getStepAttributeString(id_step, "outField");
      countryCodeField = rep.getStepAttributeString(id_step, "countryCodeField");
      checkValid = rep.getStepAttributeBoolean(id_step, "checkValid");
      findMatches = rep.getStepAttributeBoolean(id_step, "findMatches");
      region = rep.getStepAttributeString(id_step, "region");
    } catch ( Exception e ) {
      throw new KettleException( "Unable to load step from repository", e );
    }
  }
  
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    try {
      rep.saveStepAttribute( id_transformation, id_step, "inField", inField);
      rep.saveStepAttribute( id_transformation, id_step, "outField", outField);
      rep.saveStepAttribute( id_transformation, id_step, "countryCodeField", countryCodeField);
      rep.saveStepAttribute( id_transformation, id_step, "checkValid", checkValid);
      rep.saveStepAttribute( id_transformation, id_step, "findMatches", findMatches);
      rep.saveStepAttribute( id_transformation, id_step, "region", region);
    } catch ( Exception e ) {
      throw new KettleException( "Unable to save step into repository: " + id_step, e );
    }
  }
  
  public void getFields( RowMetaInterface rowMeta, String origin, RowMetaInterface[] info, StepMeta nextStep, 
    VariableSpace space, Repository repository, IMetaStore metaStore ) throws KettleStepException {
    ValueMetaInteger v0 = new ValueMetaInteger(outField);
    v0.setOrigin(origin);
    rowMeta.addValueMeta(v0);

    if(this.getCountryCodeField() != null && this.getCountryCodeField().trim().length() > 0){
      ValueMetaInteger v1 = new ValueMetaInteger(countryCodeField);
      v1.setOrigin(origin);
      rowMeta.addValueMeta(v1);
    }
  }
  
  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, 
    StepMeta stepMeta, RowMetaInterface prev, String input[], String output[],
    RowMetaInterface info, VariableSpace space, Repository repository, 
    IMetaStore metaStore ) {
    CheckResult cr;
    if ( prev == null || prev.size() == 0 ) {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_WARNING, BaseMessages.getString( PKG, "LibPhonenumberPDIPluginMeta.CheckResult.NotReceivingFields" ), stepMeta ); 
      remarks.add( cr );
    }
    else {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG, "LibPhonenumberPDIPluginMeta.CheckResult.StepRecevingData", prev.size() + "" ), stepMeta );  
      remarks.add( cr );
    }
    
    // See if we have input streams leading to this step!
    if ( input.length > 0 ) {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG, "LibPhonenumberPDIPluginMeta.CheckResult.StepRecevingData2" ), stepMeta ); 
      remarks.add( cr );
    }
    else {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString( PKG, "LibPhonenumberPDIPluginMeta.CheckResult.NoInputReceivedFromOtherSteps" ), stepMeta ); 
      remarks.add( cr );
    }
  }
  
  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta tr, Trans trans ) {
    return new LibPhonenumberPDIPlugin( stepMeta, stepDataInterface, cnr, tr, trans );
  }
  
  public StepDataInterface getStepData() {
    return new LibPhonenumberPDIPluginData();
  }

  public String getDialogClassName() {
    return "com.si.LibPhonenumberPDIPluginDialog";
  }
}
