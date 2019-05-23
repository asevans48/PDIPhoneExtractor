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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe your step plugin.
 * 
 */
public class LibPhonenumberPDIPlugin extends BaseStep implements StepInterface {
  private LibPhonenumberPDIPluginMeta meta;
  private LibPhonenumberPDIPluginData data;


  private static Class<?> PKG = LibPhonenumberPDIPluginMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$
  
  public LibPhonenumberPDIPlugin( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
    Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }
  
  /**
   * Initialize and do work where other steps need to wait for...
   *
   * @param stepMetaInterface
   *          The metadata to work with
   * @param stepDataInterface
   *          The data to initialize
   */
  public boolean init( StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface ) {
    this.data = (LibPhonenumberPDIPluginData) stepDataInterface;
    this.meta = (LibPhonenumberPDIPluginMeta) stepMetaInterface;
    return super.init( stepMetaInterface, stepDataInterface );
  }

  /**
   * Check of a nubmer is valid
   * @param protoNumbers        A list of proto numbers
   * @return                    Valid numbers if any
   */
  private List<Phonenumber.PhoneNumber> filterValid(List<Phonenumber.PhoneNumber> protoNumbers) {
    ArrayList<Phonenumber.PhoneNumber> validNumbers = new ArrayList<Phonenumber.PhoneNumber>();
    for(Phonenumber.PhoneNumber number : protoNumbers){
      if(PhoneNumberUtil.getInstance().isValidNumber(number)){
        validNumbers.add(number);
      }
    }
    return validNumbers;
  }

  /**
   * Find phone numbers in the text.
   *
   * @param sentence        The text containing numbers
   * @return                A list of proto numbers
   */
  private List<Phonenumber.PhoneNumber> findNumbers(String sentence){
    Iterable<PhoneNumberMatch> numbers = PhoneNumberUtil.getInstance().findNumbers(sentence, meta.getRegion());
    ArrayList<Phonenumber.PhoneNumber> protoNumbers = new ArrayList<Phonenumber.PhoneNumber>();
    for(PhoneNumberMatch protoMatch : numbers){
      Phonenumber.PhoneNumber number = protoMatch.number();
      protoNumbers.add(number);
    }
    return protoNumbers;
  }

  /**
   * Package an existing row
   *
   * @param r         The row object
   * @return          An updated set of rows
   */
  private ArrayList<Object[]> packageRows(List<Phonenumber.PhoneNumber> protoNumbers, Object[] r){
    ArrayList<Object[]> orows = new ArrayList<Object[]>();
    Object[] rClone = r.clone();
    if(data.outputRowMeta.size() > r.length){
      rClone = RowDataUtil.resizeArray(rClone, data.outputRowMeta.size());
    }

    int idx = data.outputRowMeta.indexOfValue(meta.getOutField());
    if(idx >= 0){
      for(Phonenumber.PhoneNumber number : protoNumbers){
        Object[] numRow = rClone.clone();
        Long phn = number.getNationalNumber();
        numRow[idx] = phn;
        if(meta.getCountryCodeField() != null && meta.getCountryCodeField().trim().length() > 0){
          Integer ccode = number.getCountryCode();
          int ccIdx = data.outputRowMeta.indexOfValue(meta.getCountryCodeField());
          numRow[ccIdx] = ccode.longValue();
        }
        orows.add(numRow);
      }
    }else{
      if(isBasic()){
        logBasic("Output Field Not Specified for PhoneNumberParser");
      }
    }
    return orows;
  }

  /**
   * Get all phone numbers
   *
   * @param rmi         The row meta
   * @param r           The row
   * @return            An arraylist of phone number rows
   */
  private ArrayList<Object[]> getPhoneNumberRows(RowMetaInterface rmi, Object[] r){
    ArrayList<Object[]> orows = new ArrayList<Object[]>();
    int idx = rmi.indexOfValue(meta.getInField());
    if(idx >= 0){
      if(meta.getRegion() != null && meta.getRegion().length() == 2){
        String text = (String) r[idx];
        List<Phonenumber.PhoneNumber> protoNumbers = new ArrayList<Phonenumber.PhoneNumber>();
        if (meta.isFindMatches()) {
          protoNumbers = findNumbers(text);
        } else {
          try {
            Phonenumber.PhoneNumber protoNumber = PhoneNumberUtil.getInstance().parse(text, meta.getRegion());
            protoNumbers.add(protoNumber);
          }catch(NumberParseException e){
            if(isBasic()){
              logBasic("Failed to parse numbers");
              logBasic(e.getMessage());
              e.printStackTrace();
            }
          }
        }

        if(meta.isCheckValid()){
          protoNumbers = filterValid(protoNumbers);
        }
        orows = packageRows(protoNumbers, r);
      }else{
        if(isBasic()){
          logBasic("2 Letter Country Code Not Provided");
        }
      }
    }else{
      if(isBasic()){
        logBasic("Output Field Not Found for Phone Number Extractor");
      }
    }
    return orows;
  }

  /**
   * Check if the value exists in the array
   *
   * @param arr  The array to check
   * @param v    The value in the array
   * @return  Whether the value exists
   */
  private int stringArrayContains(String[] arr, String v){
    int exists = -1;
    int i = 0;
    while(i < arr.length && exists == -1){
      if(arr[i].equals(v)){
        exists = i;
      }else {
        i += 1;
      }
    }
    return exists;
  }

  /**
   * Check the row meta to ensure that all fields exist.
   *
   * @param rmi         The row meta interface
   * @return            The updated row meta interface
   */
  public RowMetaInterface processRowMeta(RowMetaInterface rmi) throws KettleException{
    String[] fields = rmi.getFieldNames();
    String[] fieldnames = {meta.getOutField(), };

    int idx = stringArrayContains(fields, meta.getOutField());
    if(idx == -1){
      throw new KettleException("Sent Tokenizer missing output field");
    }

    for(int i = 0; i < fieldnames.length; i++){
      String fname = fieldnames[i];
      int cidx = stringArrayContains(fields, fname);
      if(cidx == -1){
        ValueMetaInterface value = ValueMetaFactory.createValueMeta(fname, ValueMetaInterface.TYPE_STRING);
        rmi.addValueMeta(value);
      }
    }
    return rmi;
  }

  /**
   * Setup the processor.
   *
   * @throws KettleException
   */
  private void setupProcessor() throws KettleException{
    RowMetaInterface inMeta = getInputRowMeta().clone();
    data.outputRowMeta = inMeta;
    meta.getFields(data.outputRowMeta, getStepname(), null, null, this, null, null);
    //data.outputRowMeta = processRowMeta(data.outputRowMeta);
    first = false;
  }


  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    Object[] r = getRow(); // get row, set busy!
    if ( r == null ) {
      // no more input to be expected...
      setOutputDone();
      return false;
    }

    if(first){
      setupProcessor();
    }

    ArrayList<Object[]> orows =  getPhoneNumberRows(data.outputRowMeta, r);
    if(orows.size() > 0) {
      for(Object[] row : orows){
        putRow(data.outputRowMeta, row);
      }
    }else{
      Object[] orow = RowDataUtil.resizeArray(r, data.outputRowMeta.size());
      putRow(data.outputRowMeta, orow);
    }

    if ( checkFeedback( getLinesRead() ) ) {
      if ( log.isBasic() )
        logBasic( BaseMessages.getString( PKG, "LibPhonenumberPDIPlugin.Log.LineNumber" ) + getLinesRead() );
    }
      
    return true;
  }
}