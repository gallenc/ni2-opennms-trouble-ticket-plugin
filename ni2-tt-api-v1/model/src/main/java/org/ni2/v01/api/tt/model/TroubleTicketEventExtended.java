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

import java.util.List;

//import org.ni2.v01.model.AddressCreateBodyCustomAttributesValue;

//import org.ni2.v01.model.Described;
//import org.ni2.v01.model.Documentation;
//import org.ni2.v01.model.Event;
//import org.ni2.v01.model.Party;
//import org.ni2.v01.model.Service;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TroubleTicketEventExtended {

   // @ApiModelProperty(required = true, value = "The Universal Id of the instance")
   /**
   * The Universal Id of the instance
    **/
   private String universalId;

   // @ApiModelProperty(required = true, value = "The Category of the instance")
   /**
   * The Category of the instance
    **/
   private String category;

   // @ApiModelProperty(required = true, value = "The list of classifications of the instance")
   /**
   * The list of classifications of the instance
    **/
   private List<String> classifications = new ArrayList<>();

   // @ApiModelProperty(required = true, value = "The formatted classification of the instance")
   /**
   * The formatted classification of the instance
    **/
   private String classificationPath;

   // @ApiModelProperty(required = true, value = "The Name of the instance")
   /**
   * The Name of the instance
    **/
   private String name;

   // @ApiModelProperty(required = true, value = "The map of any additional custom attribute values of the instance")
   /**
   * The map of any additional custom attribute values of the instance
    **/
   //  private Map<String, AddressCreateBodyCustomAttributesValue> customAttributes = new LinkedHashMap<>();
   private JsonNode customAttributes = JsonNodeFactory.instance.objectNode();

   // @ApiModelProperty(value = "The Description of the instance")
   /**
   * The Description of the instance
    **/
   private String description;

   // @ApiModelProperty(value = "The Long Description of the instance")
   /**
   * The Long Description of the instance
    **/
   private String longDescription;

   // @ApiModelProperty(value = "")
   //  private Party customer;
   private JsonNode customer;

   // @ApiModelProperty(value = "")
   //  private List<Service> services = new ArrayList<>();
   private List<JsonNode> services = new ArrayList<>();

   // @ApiModelProperty(value = "")
   //  private List<Described> resources = new ArrayList<>();
   private List<JsonNode> resources = new ArrayList<>();

   // @ApiModelProperty(value = "")
   //  private List<Event> parents = new ArrayList<>();
   private List<JsonNode> parents = new ArrayList<>();

   // @ApiModelProperty(value = "")
   //  private List<Event> origins = new ArrayList<>();
   private List<JsonNode> origins = new ArrayList<>();

   // @ApiModelProperty(value = "")
   //  private List<Documentation> documentations = new ArrayList<>();
   private List<JsonNode> documentations = new ArrayList<>();

   /**
   * The Universal Id of the instance
   * @return universalId
    **/
   @JsonProperty("universalId")
   public String getUniversalId() {
      return universalId;
   }

   public void setUniversalId(String universalId) {
      this.universalId = universalId;
   }

   /**
   * The Category of the instance
   * @return category
    **/
   @JsonProperty("category")
   public String getCategory() {
      return category;
   }

   public void setCategory(String category) {
      this.category = category;
   }

   /**
   * The list of classifications of the instance
   * @return classifications
    **/
   @JsonProperty("classifications")
   public List<String> getClassifications() {
      return classifications;
   }

   public void setClassifications(List<String> classifications) {
      this.classifications = classifications;
   }

   /**
   * The formatted classification of the instance
   * @return classificationPath
    **/
   @JsonProperty("classificationPath")
   public String getClassificationPath() {
      return classificationPath;
   }

   public void setClassificationPath(String classificationPath) {
      this.classificationPath = classificationPath;
   }

   /**
   * The Name of the instance
   * @return name
    **/
   @JsonProperty("name")
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   /**
   * The map of any additional custom attribute values of the instance
   * @return customAttributes
    **/
   @JsonProperty("customAttributes")
   public JsonNode getCustomAttributes() {
      return customAttributes;
   }

   public void setCustomAttributes(JsonNode customAttributes) {
      this.customAttributes = customAttributes;
   }

   /**
   * The Description of the instance
   * @return description
    **/
   @JsonProperty("description")
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   /**
   * The Long Description of the instance
   * @return longDescription
    **/
   @JsonProperty("longDescription")
   public String getLongDescription() {
      return longDescription;
   }

   public void setLongDescription(String longDescription) {
      this.longDescription = longDescription;
   }

   /**
   * Get customer
   * @return customer
    **/
   @JsonProperty("customer")
   public JsonNode getCustomer() {
      return customer;
   }

   public void setCustomer(JsonNode customer) {
      this.customer = customer;
   }

   /**
   * Get services
   * @return services
    **/
   @JsonProperty("services")
   public List<JsonNode> getServices() {
      return services;
   }

   public void setServices(List<JsonNode> services) {
      this.services = services;
   }

   /**
   * Get resources
   * @return resources
    **/
   @JsonProperty("resources")
   public List<JsonNode> getResources() {
      return resources;
   }

   public void setResources(List<JsonNode> resources) {
      this.resources = resources;
   }

   /**
   * Get parents
   * @return parents
    **/
   @JsonProperty("parents")
   public List<JsonNode> getParents() {
      return parents;
   }

   public void setParents(List<JsonNode> parents) {
      this.parents = parents;
   }

   /**
   * Get origins
   * @return origins
    **/
   @JsonProperty("origins")
   public List<JsonNode> getOrigins() {
      return origins;
   }

   public void setOrigins(List<JsonNode> origins) {
      this.origins = origins;
   }

   /**
   * Get documentations
   * @return documentations
    **/
   @JsonProperty("documentations")
   public List<JsonNode> getDocumentations() {
      return documentations;
   }

   public void setDocumentations(List<JsonNode> documentations) {
      this.documentations = documentations;
   }

   // convenience methods to access custom attributes for trouble ticket

   @JsonIgnore
   public String getAlarmSource() {
      return customAttributes.get("AlarmSource").asText();
   }

   @JsonIgnore
   public String getAlarmId() {
      return customAttributes.get("AlarmId").asText();
   }

   @JsonIgnore
   public String getStatus() {
      return customAttributes.get("Status").asText();
   }

   @JsonIgnore
   public String getTTCategory() {
      return customAttributes.get("Category").asText();
   }

   @JsonIgnore
   public List<String> getResourceIds() {
      List<String> resourceIds = new ArrayList<String>();
      for (JsonNode resource : resources) {
         String id = resource.get("universalId").asText();
         resourceIds.add(id);
      }
      return resourceIds;
   }

   @JsonIgnore
   public void setTTCategory(String category) {
      ObjectNode on = (ObjectNode) customAttributes;
      on.put("Category", category);
   }

   @JsonIgnore
   public void setAlarmSource(String alarmSource) {
      ObjectNode on = (ObjectNode) customAttributes;
      on.put("AlarmSource", alarmSource);
   }

   @JsonIgnore
   public void setAlarmId(String alarmId) {
      ObjectNode on = (ObjectNode) customAttributes;
      on.put("AlarmId", alarmId);
   }

   @JsonIgnore
   public void setStatus(String status) {
      ObjectNode on = (ObjectNode) customAttributes;
      on.put("Status", status);
   }

   @JsonIgnore
   public void setResourceIds(List<String> resourceIds) {
      // creates resources with some of the fields populated
      //     "resources": [
      //                   {
      //                       "customAttributes": {
      //                           "Status": "In Process",
      //                           "Identifier": "FUN-00002497",
      //                           "NamingConvention": "oneTimeFree",
      //                           "LastModificationDate": 1718279484392,
      //                           "UniversalId": "monaco_01",
      //                           "Name": "Monaco 01"
      //                       },
      //                       "category": "Function",
      //                       "classifications": [
      //                           "Function",
      //                           "Function",
      //                           "Network",
      //                           "Generic Node",
      //                           "EMS Node"
      //                       ],
      //                       "classificationPath": "Function(\"Function/Network/Generic Node/EMS Node\")",
      //                       "name": "Monaco 01",
      //                       "universalId": "monaco_01"
      //                   }
      //               ]

      for (String resourceId : resourceIds) {
         ObjectNode resource = JsonNodeFactory.instance.objectNode();
         resource.put("classificationPath", "Function(\"Function/Network/Generic Node/EMS Node\")");
         resource.put("universalId", resourceId);
         resource.put("name", resourceId);
         resource.put("Category", "EMS Node");
         resources.add(resource);
      }

   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("class EventExtended {\n");

      sb.append("    universalId: ").append(toIndentedString(universalId)).append("\n");
      sb.append("    category: ").append(toIndentedString(category)).append("\n");
      sb.append("    classifications: ").append(toIndentedString(classifications)).append("\n");
      sb.append("    classificationPath: ").append(toIndentedString(classificationPath)).append("\n");
      sb.append("    name: ").append(toIndentedString(name)).append("\n");
      sb.append("    customAttributes: ").append(toIndentedString(customAttributes)).append("\n");
      sb.append("    description: ").append(toIndentedString(description)).append("\n");
      sb.append("    longDescription: ").append(toIndentedString(longDescription)).append("\n");
      sb.append("    customer: ").append(toIndentedString(customer)).append("\n");
      sb.append("    services: ").append(toIndentedString(services)).append("\n");
      sb.append("    resources: ").append(toIndentedString(resources)).append("\n");
      sb.append("    parents: ").append(toIndentedString(parents)).append("\n");
      sb.append("    origins: ").append(toIndentedString(origins)).append("\n");
      sb.append("    documentations: ").append(toIndentedString(documentations)).append("\n");
      sb.append("}");
      return sb.toString();
   }

   /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
   private static String toIndentedString(Object o) {
      if (o == null) {
         return "null";
      }
      return o.toString().replace("\n", "\n    ");
   }
}
