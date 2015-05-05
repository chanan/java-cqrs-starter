package controllers;

import actors.ClusterSystem;
import actors.GameActor;
import akka.actor.ActorRef;
import com.example.protocols.Commands;
import com.example.protocols.Queries;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;


public class Application extends Controller {
    private static ActorRef gameActor = ClusterSystem.getActorSystem().actorOf(GameActor.props());

    public static Result index() {
        final String message = flash("message");
        return ok(index.render(message));
    }

    public static Result newTeam() {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String teamName = requestData.get("teamName");
        gameActor.tell(new Commands.NewTeam(teamName), ActorRef.noSender());
        flash("message", "Team Created");
        return redirect("/");
    }

    public static Result newPlayer() {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String teamName = requestData.get("teamName");
        final String name = requestData.get("name");
        final int jerseyNumber = Integer.parseInt(requestData.get("jerseyNumber"));
        gameActor.tell(new Commands.AddPlayer(teamName, jerseyNumber, name), ActorRef.noSender());
        flash("message", "Player Added");
        return redirect("/");
    }

    public static Result score() {
        final DynamicForm requestData = Form.form().bindFromRequest();
        final String teamName = requestData.get("teamName");
        final int jerseyNumber = Integer.parseInt(requestData.get("jerseyNumber"));
        final int score = Integer.parseInt(requestData.get("score"));
        gameActor.tell(new Commands.PlayerScore(teamName, jerseyNumber, score), ActorRef.noSender());
        flash("message", "Player scored");
        return redirect("/");
    }

    public static Result player() {
        /*return wrap(ask(gameActor, new QueryProtocols.PlayerNameRequest("Test", 1), 1000)).map(obj ->
                        ok(Json.toJson(obj))
        );*/
        gameActor.tell(new Queries.PlayerNameRequest("Test", 1), ActorRef.noSender());
        return ok();

    }
}