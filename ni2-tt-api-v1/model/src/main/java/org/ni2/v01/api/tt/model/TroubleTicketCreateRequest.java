/*
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * {
 *    "classificationPath": "Event(\"Event/Support/Incident/Monitoring Incident\")",
 *    "description": "{{description}}",
 *    "longDescription": "{{longDescription}}",
 *    "customAttributes": {
 *       "Category": "{{categoryUpdated}}",
 *       "AlarmSource": "{{alarmSource}}",
 *       "AlarmId": "{{alarmId}}"
 *    },
 *    "resourceIds": ["{{resourceUID}}"]
 * }
 */
public class TroubleTicketCreateRequest {
   
   private String classificationPath = "Event(\"Event/Support/Incident/Monitoring Incident\")";
   
   private  String description =null;
   
   private String longDescription=null;
   
   private  List<String>  resourceIds = new ArrayList<String>();
   
   private Map<String,String> customAttributes = new LinkedHashMap<String, String>();
   
   public String getClassificationPath() {
      return classificationPath;
   }

   /**
    * do not set manually 
    * @param classificationPath
    */
   public void setClassificationPath(String classificationPath) {
      this.classificationPath = classificationPath;
   }

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

   public List<String> getResourceIds() {
      return resourceIds;
   }

   public void setResourceIds(List<String> resourceIds) {
      this.resourceIds = resourceIds;
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

   /**
    * Note toString does not use customAttributes but only getters for ticket request. Can be used as simple equals()
    */
   @Override
   public String toString() {
      return "TroubleTicketCreateRequest [getClassificationPath()=" + getClassificationPath() + ", getDescription()=" + getDescription()
               + ", getLongDescription()=" + getLongDescription() + ", getResourceIds()=" + getResourceIds() + ", getCategory()=" + getTTCategory()
               + ", getAlarmSource()=" + getAlarmSource() + ", getAlarmId()=" + getAlarmId() + "]";
   }
  
   
}
