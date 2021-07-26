package Blackjack;

public class EvenLover extends Player {

    public EvenLover(String name)
    {
        super(name);
    }

    public Integer move()
    {
        return Game.CARDS_COUNT_COMMAND;
    }

    public Integer move(int cardLeft)
    {
        if(cardLeft % 2 == 1)
            return Game.HIT_COMMAND;
        return Game.STAND_COMMAND;
    }

    public Integer move(Card croupierCard)
    {
        return Game.STAND_COMMAND;
    }
}
