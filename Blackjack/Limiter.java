package Blackjack;

public class Limiter extends Player {

    private Integer limit;

    public Limiter(String name, int limit)
    {
        super(name);
        this.limit = limit;
    }

    public Integer move()
    {
        if(getPoints() < limit)
            return Game.HIT_COMMAND;
        return Game.STAND_COMMAND;
    }

    public Integer move(int cardLeft)
    {
        return move();
    }

    public Integer move(Card croupierCard)
    {
        return move();
    }
}
