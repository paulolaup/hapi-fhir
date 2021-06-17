package ca.uhn.fhir.jpa.delete.model;

/*-
 * #%L
 * HAPI FHIR JPA Server
 * %%
 * Copyright (C) 2014 - 2021 Smile CDR, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ca.uhn.fhir.interceptor.model.RequestPartitionId;
import ca.uhn.fhir.model.api.IModelJson;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Serialize a list of URLs and partition ids so Spring Batch can store it as a String
 */
public class RequestListJson implements IModelJson {
	static final ObjectMapper ourObjectMapper = new ObjectMapper();

	@JsonProperty("partitionedUrls")
	private List<PartitionedUrl> myPartitionedUrls;

	public static RequestListJson fromUrlStringsAndRequestPartitionIds(List<String> theUrls, List<RequestPartitionId> theRequestPartitionIds) {
		assert theUrls.size() == theRequestPartitionIds.size();

		RequestListJson retval = new RequestListJson();
		List<PartitionedUrl> partitionedUrls = new ArrayList<>();
		for (int i = 0; i < theUrls.size(); ++i) {
			partitionedUrls.add(new PartitionedUrl(theUrls.get(i), theRequestPartitionIds.get(i)));
		}
		retval.setPartitionedUrls(partitionedUrls);
		return retval;
	}

	public static RequestListJson fromJson(String theJson) {
		try {
			return ourObjectMapper.readValue(theJson, RequestListJson.class);
		} catch (JsonProcessingException e) {
			throw new InternalErrorException("Failed to decode " + RequestListJson.class);
		}
	}

	@Override
	public String toString() {
		try {
			return ourObjectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new InvalidRequestException("Failed to encode " + RequestListJson.class, e);
		}
	}

	public List<PartitionedUrl> getPartitionedUrls() {
		return myPartitionedUrls;
	}

	public void setPartitionedUrls(List<PartitionedUrl> thePartitionedUrls) {
		myPartitionedUrls = thePartitionedUrls;
	}
}
