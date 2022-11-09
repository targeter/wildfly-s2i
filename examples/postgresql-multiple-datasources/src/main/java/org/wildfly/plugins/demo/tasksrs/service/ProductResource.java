/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.plugins.demo.tasksrs.service;

import org.wildfly.plugins.demo.tasksrs.model.Product;
import org.wildfly.plugins.demo.tasksrs.model.ProductDaoImpl;
import org.wildfly.plugins.demo.tasksrs.model.Task;
import org.wildfly.plugins.demo.tasksrs.model.TaskDaoImpl;
import org.wildfly.plugins.demo.tasksrs.model.TaskUser;
import org.wildfly.plugins.demo.tasksrs.model.TaskUserDaoImpl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.security.Principal;
import java.util.List;

/**
 * A JAX-RS resource for exposing REST endpoints for Task manipulation
 */
@RequestScoped
@Path("/products/")
public class ProductResource {

    @Inject
    private ProductDaoImpl productDao;

    @Inject
    private UserTransaction tx;

    @POST
    @Path("name/{name}")
    public Response createProduct(@Context UriInfo info, @Context SecurityContext context,
            @PathParam("name") @DefaultValue("Ball") String productName) throws Exception {
        Product product = null;
        try {
            tx.begin();
            product = new Product(productName);
            productDao.createProduct(product);
            tx.commit();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        String rawPath = info.getAbsolutePath().getRawPath()
                .replace("name/" + product.getName(), "id/" + product.getId().toString());
        UriBuilder uriBuilder = info.getAbsolutePathBuilder().replacePath(rawPath);
        URI uri = uriBuilder.build();

        return Response.created(uri).build();
    }

    @GET
    // JSON: include "application/json" in the @Produces annotation to include json support
    // @Produces({ "application/xml", "application/json" })
    @Produces({"application/xml"})
    public List<Product> getProducts(@Context SecurityContext context) {
        try {
            tx.begin();
            List<Product> products = productDao.getAll();
            tx.commit();
            return products;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
