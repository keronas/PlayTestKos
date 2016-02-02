package controllers;

import models.LoggyError;
import org.apache.commons.lang3.StringUtils;
import play.*;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.*;
import views.html.*;

import javax.inject.Inject;
import java.util.LinkedList;

public class Loggy extends Controller {

    @Inject WSClient ws;

    static LinkedList<LoggyError> errors = new LinkedList<>();

    public Promise<Result> restTest() {
        WSRequest request = ws.url("http://localhost/rest/project/all");
        Promise<WSResponse> promise = request.get();

        return promise.map(response -> ok(response.getBody()));
    }

    public Result mainView() {
        return ok(loggy.render(errors));
    }

    public Promise<Result> upload(String id) {

        if (!StringUtils.isNumeric(id)) {
            return Promise.promise(() -> badRequest());
        }
        int eId = Integer.parseInt(id);

        if (errors.size() <= eId) {
            return Promise.promise(() -> badRequest());
        }

        LoggyError e = errors.get(eId);

        WSRequest request = ws.url(Play.application().configuration().getString("loggy.youtrackurl") + "/rest/issue");
        request.setQueryParameter("project", Play.application().configuration().getString("loggy.project"));
        request.setQueryParameter("summary", e.getSummmary());
        request.setQueryParameter("description", e.getDescription());
        Promise<WSResponse> promise = request.put("");

        return promise.map(response -> checkUpload(response, e));
    }

    public Result deleteAll() {
        errors.clear();
        return redirect("/logs");
    }

    public Result errorRequest(String summary, String description) {
        error(description, summary);
        return redirect("/logs");
    }

    public static void error(String content) {
        error(content, "Unnamed Error");
    }

    public static void error(String content, String summary) {
        errors.add(new LoggyError(summary, content));
    }

    private Result checkUpload(WSResponse response, LoggyError error) {
        if (response.getStatus() == 201) {
            error.url = response.getHeader("Location").replace("/rest", "");
            return redirect("/logs");
        }

        return badRequest(response.getBody());
    }
}


