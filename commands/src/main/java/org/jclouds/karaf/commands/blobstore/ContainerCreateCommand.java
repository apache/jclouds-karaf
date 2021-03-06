/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jclouds.karaf.commands.blobstore;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.domain.Location;

@Command(scope = "jclouds", name = "blobstore-container-create", description = "Creates a container")
public class ContainerCreateCommand extends BlobStoreCommandWithOptions {

   @Argument(index = 0, name = "containerNames", description = "The name of the container", required = true, multiValued = true)
   List<String> containerNames;

   @Option(name = "-l", aliases = "--location", description = "Location to create container in", required = false, multiValued = false)
   String locationString = "";

   @Override
   protected Object doExecute() throws Exception {
      BlobStore blobStore = getBlobStore();

      Location location = null;
      if (!locationString.isEmpty()) {
         for (Location loc : blobStore.listAssignableLocations()) {
            if (loc.getId().equalsIgnoreCase(locationString)) {
               location = loc;
               break;
            }
         }
         if (location == null) {
            throw new IllegalArgumentException("unknown location: " + locationString);
         }
      }

      for (String container : containerNames) {
         boolean created = blobStore.createContainerInLocation(location, container);
         if (!created) {
            if (blobStore.containerExists(container)) {
               throw new Exception("Container already exists: " + container);
            }

            throw new Exception("Could not create container: " + container);
         }
      }
      return null;
   }
}
