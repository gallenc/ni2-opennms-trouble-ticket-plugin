/*
 * Copyright (c) 2024 Ni2 Inc., Entimoss Ltd.
 * Licensed to The OpenNMS Group, Inc (TOG) under one or more
 * contributor license agreements.  See the LICENSE.md file
 * distributed with this work for additional information
 * regarding copyright ownership.
 *
 * TOG licenses this file to You under the GNU Affero General
 * Public License Version 3 (the "License") or (at your option)
 * any later version.  You may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at:
 *
 *      https://www.gnu.org/licenses/agpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */

package org.ni2.v01.api.tt.model;


import java.util.LinkedHashMap;
import java.util.Map;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * {
 *    "description": "{{description}}",
 *    "longDescription": "{{longDescription}}",
 *    "customAttributes": {
 *       "Category": "{{categoryUpdated}}",
 *       "AlarmSource": "{{alarmSource}}",
 *       "AlarmId": "{{alarmId}}",
 *       "AlarmSeverity": "{{alarmSeverity}}",
 *       "AlarmStatus": "{{alarmStatus}}"
  *   }
 * }
 */
public class TroubleTicketUpdateRequest {
   
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private  String description =null;
   
   @JsonInclude(JsonInclude.Include.NON_NULL)
   private String longDescription=null;
   
   private Map<String,String> customAttributes = new LinkedHashMap<String, String>();
   
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getLongDescription() {
      return longDescription;
   }

   public void setLongDescription(String longDescription) {
      this.longDescription = longDescription;
   }

   public Map<String, String> getCustomAttributes() {
      return customAttributes;
   }

   public void setCustomAttributes(Map<String, String> customAttributes) {
      this.customAttributes = customAttributes;
   }

   @JsonIgnore
   public void setTTCategory(String category) {
      customAttributes.put("Category", category);
   }
   
   @JsonIgnore
   public void setAlarmSource(String alarmSource) {
      customAttributes.put("AlarmSource", alarmSource);
   }
   
   @JsonIgnore
   public void setAlarmId( String alarmId ) {
      customAttributes.put("AlarmId", alarmId);
   }
   
   /**
    * 
    * @param alarmSeverity must be one of TroubleTicketEventExtended.VALID_ALARM_STATUS
    */
   @JsonIgnore
   public void setAlarmSeverity(String alarmSeverity) {
      if(! TroubleTicketEventExtended.VALID_ALARM_SEVERITIES.contains(alarmSeverity)) {
         throw new IllegalArgumentException ("alarmSeverity must be one of "+TroubleTicketEventExtended.VALID_ALARM_SEVERITIES);
      }
      customAttributes.put("AlarmSeverity", alarmSeverity);
   }

   /**
    * 
    * @param alarmStatus must be one of TroubleTicketEventExtended.VALID_ALARM_SEVERITIES
    */
   @JsonIgnore
   public void setAlarmStatus(String alarmStatus) {
      if(! TroubleTicketEventExtended.VALID_ALARM_STATUS.contains(alarmStatus)) {
         throw new IllegalArgumentException ("alarmSeverity must be one of "+TroubleTicketEventExtended.VALID_ALARM_STATUS);
      }
      customAttributes.put("AlarmStatus", alarmStatus);
   }
   
   @JsonIgnore
   public String getTTCategory() {
      return customAttributes.get("Category");
   }
   
   @JsonIgnore
   public String getAlarmSource() {
      return customAttributes.get("AlarmSource");
   }
   
   @JsonIgnore
   public String getAlarmId() {
      return customAttributes.get("AlarmId");
   }
  
   @JsonIgnore
   public String getAlarmSeverity() {
      return customAttributes.get("AlarmSeverity");
   }
   
   @JsonIgnore
   public String getAlarmStatus() {
      return customAttributes.get("AlarmStatus");
   }


   /**
    * Note toString does not use customAttributes but only getters for ticket request. Can be used as simple equals()
    */
   @Override
   public String toString() {
      return "TroubleTicketUpdateRequest [ getDescription()=" + getDescription()
               + ", getLongDescription()=" + getLongDescription() + ", getCategory()=" + getTTCategory()
               + ", getAlarmSource()=" + getAlarmSource() + ", getAlarmId()=" + getAlarmId() + ", getAlarmSeverity()="+ getAlarmSeverity() 
               + ", getAlarmStatus()="+ getAlarmStatus()+"]";
   }
  
   
}
