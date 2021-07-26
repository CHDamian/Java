package Blackjack;

public abstract class Player {

    private String name;
    protected Hand myHand;

    public Player(String name)
    {
        this.name = name;
        this.myHand = new Hand();
    }


    public final String getName()
    {
        return name;
    }

    public final Integer getPoints()
    {
        return myHand.getPoints();
    }

    public final void takeCard(Card c)
    {
        myHand.takeCard(c);
    }

    public final void handReset()
    {
        myHand.reset();
    }



    public abstract Integer move();
    public abstract Integer move(int cardLeft);
    public abstract Integer move(Card croupierCard);

}
