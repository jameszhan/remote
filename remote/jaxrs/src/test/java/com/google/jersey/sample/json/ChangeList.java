package com.google.jersey.sample.json;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;

import com.sun.jersey.api.json.JSONWithPadding;

@Path("/changes")
@Produces({"application/x-javascript", "application/json", "application/xml"})
public class ChangeList {

	static final List<ChangeRecordBean> changes = new LinkedList<ChangeRecordBean>();

	static {
		changes.add(new ChangeRecordBean(false, 2, "title \"User Guide\" updated"));
		changes.add(new ChangeRecordBean(true, 1, "fixed metadata"));
		changes.add(new ChangeRecordBean(false, 91, "added index"));
		changes.add(new ChangeRecordBean(false, 650, "\"Troubleshoothing\" chapter"));
		changes.add(new ChangeRecordBean(false, 1, "fixing typo"));
	}

	@GET
	public JSONWithPadding getChanges(@QueryParam("callback") String callback, @QueryParam("type") int type) {
		return new JSONWithPadding(new GenericEntity<List<ChangeRecordBean>>(changes) {
		}, callback);
	}

	@GET
	@Path("latest")
	public JSONWithPadding getLastChange(@QueryParam("callback") String callback, @QueryParam("type") int type) {
		return new JSONWithPadding(changes.get(changes.size() - 1), callback);
	}

}
