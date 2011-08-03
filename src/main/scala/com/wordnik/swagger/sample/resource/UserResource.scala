/**
 *  Copyright 2011 Wordnik, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.wordnik.swagger.sample.resource

import com.wordnik.swagger.core.ApiOperation._
import com.wordnik.swagger.core.ApiError._
import com.wordnik.swagger.core._
import com.wordnik.swagger.core.ApiParam._
import com.wordnik.swagger.sample.model.User
import com.wordnik.swagger.sample.data.UserData
import com.wordnik.swagger.sample.exception.NotFoundException

import com.sun.jersey.spi.resource.Singleton

import javax.ws.rs.core.Response
import javax.ws.rs._
import util.RestResourceUtil
/**
 * User: ramesh
 * Date: 7/29/11
 * Time: 5:23 PM
 */

trait UserResource extends RestResourceUtil {
  var userData = new UserData

  @POST
  @ApiOperation(value = "Create user", notes = "This can only be done the logged in user")
  def createUser(
    @ApiParam(value = "Created user object", required = true) user: User) = {
    userData.addUser(user)
    Response.ok.entity("").build
  }

  @PUT
  @Path("/{username}")
  @ApiOperation(value = "Updated user", notes = "This can only be done the logged in user")
  @ApiErrors(Array(
    new ApiError(code = 400, reason = "Invalid username supplied"),
    new ApiError(code = 404, reason = "User not found")))
  def updateUser(
    @ApiParam(value = "name that need to be deleted", required = true)@PathParam("username") username: String,
    @ApiParam(value = "Updated user object", required = true) user: User) = {
    userData.addUser(user)
    Response.ok.entity("").build
  }

  @DELETE
  @Path("/{username}")
  @ApiOperation(value = "Delete user", notes = "This can only be done the logged in user")
  @ApiErrors(Array(
    new ApiError(code = 400, reason = "Invalid username supplied"),
    new ApiError(code = 404, reason = "User not found")))
  def deleteUser(
    @ApiParam(value = "name that need to be deleted", required = true)@PathParam("username") username: String) = {
    userData.removeUser(username)
    Response.ok.entity("").build
  }

  @GET
  @Path("/{username}")
  @ApiOperation(value = "Get user by user name", responseClass = "com.wordnik.swagger.sample.model.User")
  @ApiErrors(Array(
    new ApiError(code = 400, reason = "Invalid username supplied"),
    new ApiError(code = 404, reason = "User not found")))
  def getUserByName(
    @ApiParam(value = "name that need to be fetched", required = true)@PathParam("username") username: String) = {
    var user = userData.findUserByName(username)
    if (null != user) {
      Response.ok.entity(user).build
    } else {
      throw new NotFoundException(404, "User not found")
    }
  }

  @GET
  @Path("/login")
  @ApiOperation(value = "Login user into the system", responseClass = "String")
  @ApiErrors(Array(
    new ApiError(code = 400, reason = "Invalid username and password combination")))
  def loginUser(
    @ApiParam(value = "user name for login", required = true)@QueryParam("username") username: String,
    @ApiParam(value = "password for login in clear text", required = true)@QueryParam("password") password: String) = {
    Response.ok.entity("logged in user session:" + System.currentTimeMillis()).build
  }

  @GET
  @Path("/logout")
  @ApiOperation(value = "Logs out current logged in user session")
  def logoutUser() = {
    Response.ok.entity("").build
  }
}

@Path("/user.json")
@Singleton
@Api("/user")
@Produces(Array("application/json"))
class UserResourceJSON extends Help
  with UserResource

@Path("/user.xml")
@Singleton
@Api("/user")
@Produces(Array("application/xml"))
class UserResourceXML extends Help
  with UserResource