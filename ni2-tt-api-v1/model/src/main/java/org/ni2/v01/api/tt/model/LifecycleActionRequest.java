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

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * {
 *  "action": "Cancel",   // can be Close Cancel Resolve
 *  "comment": "{{closeComment}}"
 *  }
 */
public class LifecycleActionRequest {
   
   @JsonIgnore
   public static final String CLOSE = "Close";
   
   @JsonIgnore
   public static final String CANCEL = "Cancel";
   
   @JsonIgnore
   public static final String RESOLVE = "Resolve";
   
   public static final List<String> ALLOWED_ACTIONS_LIST = Arrays.asList(CLOSE,CANCEL,RESOLVE);
   
   private String action = null;
   private String comment = null;
   
   public String getAction() {
      return action;
   }
   public void setAction(String action) {
      if (!ALLOWED_ACTIONS_LIST.contains(action)) throw new IllegalArgumentException("action must be one of "+ALLOWED_ACTIONS_LIST);
      this.action = action;
   }
   public String getComment() {
      return comment;
   }
   public void setComment(String comment) {
      this.comment = comment;
   }
   
   @Override
   public String toString() {
      return "LifecycleActionRequest [action=" + action + ", comment=" + comment + "]";
   }
   
   

}
