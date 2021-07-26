package Blackjack;

public class Stander extends Player {

    public Stander(String name)
    {
        super(name);
    }

    public Integer move()
    {
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
