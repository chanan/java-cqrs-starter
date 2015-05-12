package controllers;

import actors.ClusterSystem;
import actors.CommandActor;
import actors.QueryActor;
import akka.actor.ActorRef;
import com.example.protocols.Commands;
import com.example.protocols.Queries;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import static akka.pattern.Patterns.ask;
import static play.libs.F.Promise.wrap;

public class Application extends Controller {
    private static ActorRef commandActor = ClusterSystem.getCommandSystem().actorOf(CommandActor.props());
    private static ActorRef queryActor = ClusterSystem.getQuerySystem().actorOf(QueryActor.props());

    public static Result index() {
        final String message = flash("message");
        return ok(index.render(message));
    }

    public static Result newTeam() {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String teamName = requestData.get("teamName");
        commandActor.tell(new Commands.NewTeam(teamName), ActorRef.noSender());
        flash("message", "Team Created");
        return redirect("/");
    }

    public static Result newPlayer() {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String teamName = requestData.get("teamName");
        final String name = requestData.get("name");
        final int jerseyNumber = Integer.parseInt(requestData.get("jerseyNumber"));
        commandActor.tell(new Commands.AddPlayer(teamName, jerseyNumber, name), ActorRef.noSender());
        flash("message", "Player Added");
        return redirect("/");
    }

    public static Result score() {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String teamName = requestData.get("teamName");
        final int jerseyNumber = Integer.parseInt(requestData.get("jerseyNumber"));
        final int score = Integer.parseInt(requestData.get("score"));
        commandActor.tell(new Commands.PlayerScore(teamName, jerseyNumber, score), ActorRef.noSender());
        flash("message", "Player scored");
        return redirect("/");
    }

    public static F.Promise<Result> player() {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String teamName = requestData.get("teamName");
        final int jerseyNumber = Integer.parseInt(requestData.get("jerseyNumber"));
        return wrap(ask(queryActor, new Queries.PlayerRowRequest(teamName, jerseyNumber), 1000)).map(obj ->
                        ok(Json.toJson(obj))
        );
    }

    public static F.Promise<Result> team() {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String teamName = requestData.get("teamName");
        return wrap(ask(queryActor, new Queries.TeamScoreRequest(teamName), 1000)).map(obj ->
                ok(Json.toJson(obj))
        );
    }
}