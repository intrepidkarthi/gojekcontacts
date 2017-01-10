/**
 * Copyright 2016 Erik Jhordan Rey.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gazematic.gojekcontacts.data;

import com.gazematic.gojekcontacts.model.Contact;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Karthi on 1/6/2017.
 */

public interface KontakAPIInterface {

  @GET("contacts.json")
  Call<List<Contact>> getContactsList();

  @POST("contacts.json")
  Call<Contact> setContactsList(@Body Contact contact);

  @GET("contacts/{id}.json")
  Call<Contact> getContact(@Path("id") int contactId);

  @PUT("contacts/{id}.json")
  Call<Contact> putContactsList(@Path("id") long contactId, @Body Contact contact);


}
