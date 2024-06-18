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

import java.util.Date;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ErrorResponse {

   private Integer statusCode = null;
   private String code = null;
   private String timestamp = null;
   private String path = null;
   private String description = null;
   
   public Integer getStatusCode() {
      return statusCode;
   }

   public void setStatusCode(Integer statusCode) {
      this.statusCode = statusCode;
   }

   public String getCode() {
      return code;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getTimestamp() {
      return timestamp;
   }
   
   public void setTimestamp(String timestamp) {
      this.timestamp = timestamp;
   }

   public String getPath() {
      return path;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
   
   @JsonIgnore
   public void setTimestampDate(Date date) {
      String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
      setTimestamp(simpleDateFormat.format(date));
   }


   @Override
   public String toString() {
      return "ErrorResponse [statusCode=" + statusCode + ", code=" + code + ", timestamp=" + timestamp + ", path=" + path + ", description=" + description + "]";
   }

}
