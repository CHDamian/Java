package Blackjack;

public class Game {

    private final Integer MAX_POINTS = 21;
    private final Integer CROUPIER_LIMIT = 17;

    private final Integer NUM_OF_PLAYERS = 4;
    private final Integer CARDS_TO_DRAW = 2;

    private Deck cards;
    private Integer turnsLeft;
    private Player[] players;
    private Integer[] wins;
    private Card croupierVisibleCard;
    private Hand croupierHand;


    private final Integer NO_COMMAND = -1;

    public final static Integer STAND_COMMAND = 0;
    public final static Integer HIT_COMMAND = 1;
    public final static Integer CARDS_COUNT_COMMAND = 2;
    public final static Integer CROUPIER_CARD = 3;

    public Game()
    {
        turnsLeft = 100;
        try {
            cards = new Deck(50);
        }
        catch (Deck.NotPositiveNumberOfSets exception)
        {
            System.exit(1);
        }
        makePlayers();

    }

    private void makePlayers()
    {
        players = new Player[4];
        wins = new Integer[4];

        players[0] = new Stander("Mike");
        players[1] = new Limiter("Barry", 15);
        players[2] = new EvenLover("Jhin");
        players[3] = new Strategist("Albert");

        for(int i=0; i<NUM_OF_PLAYERS; i++)
            wins[i] = 0;

        croupierHand = new Hand();
    }

    public static void main(String args[])
    {
        Game simulation = new Game();
        simulation.gameplay();

    }

    public void gameplay()
    {
        System.out.println("GAME START!");
        System.out.println();
        System.out.println();
        while (turnsLeft > 0)
        {
            turnPrepare();
            for (int i = 0; i < NUM_OF_PLAYERS; i++) {
                playerHandle(players[i]);
            }
            System.out.println();

            croupierHandle();
            System.out.println();

            pointsHandle();
            System.out.println();
            System.out.println();
        }
        gameFinish();
    }

    private void turnPrepare()
    {
        System.out.println("Turns left: "+ turnsLeft);
        turnsLeft--;
        System.out.println();
        System.out.println();

        for (int i=0; i < NUM_OF_PLAYERS; i++)
        {
            players[i].handReset();
            dealToPlayer(players[i]);
        }
        System.out.println();

        croupierHand.reset();
        croupierVisibleCard = cards.draw();
        System.out.println("Croupier drew " + croupierVisibleCard.getName());
        croupierHand.takeCard(croupierVisibleCard);
        croupierHand.takeCard(cards.draw());

        System.out.println();
        System.out.println();

    }

    private void dealToPlayer(Player player)
    {
        for (int i = 0; i < CARDS_TO_DRAW; i++)
        {
            playerDraw(player);
        }
        System.out.println(player.getName() + " has " + player.getPoints() + " points");
        System.out.println();

    }

    private void playerDraw(Player player)
    {
        Card drawn = cards.draw();
        System.out.println(player.getName() + " drew " + drawn.getName());
        player.takeCard(drawn);
    }

    private void playerHandle(Player player)
    {
        System.out.println(player.getName() + "'s turn");
        Integer command = NO_COMMAND;
        while(command != 0)
        {
            if(command == NO_COMMAND)
            {
                command = player.move();
                if (command < 0 || command > 3)
                    command = STAND_COMMAND;
            }
            else if(command == HIT_COMMAND)
            {
                playerDraw(player);
                System.out.println(player.getName() + " has " + player.getPoints() + " points");
                if(player.getPoints() > MAX_POINTS)
                {
                    command = STAND_COMMAND;
                }
                else command = NO_COMMAND;
            }
            else if(command == CARDS_COUNT_COMMAND)
            {
                command = player.move(cards.cardsLeft());
                if (command < 0 || command > 3)
                    command = STAND_COMMAND;
            }
            else if (command == CROUPIER_CARD)
            {
                command = player.move(croupierVisibleCard);
                if (command < 0 || command > 3)
                    command = STAND_COMMAND;
            }

            if(player.getPoints() >= MAX_POINTS)
                command = STAND_COMMAND;
        }
        System.out.println(player.getName() + "'s turn end with " + player.getPoints() + " points");
        System.out.println();
    }

    private void croupierHandle()
    {
        while(croupierHand.getPoints() < CROUPIER_LIMIT)
        {
            Card drawn = cards.draw();
            System.out.println("Croupier drew " + drawn.getName());
            croupierHand.takeCard(drawn);
        }
        System.out.println("Croupier has " + croupierHand.getPoints() + " points");
    }

    private void pointsHandle()
    {
        System.out.println("Turn result:");
        for (int i=0; i < NUM_OF_PLAYERS; i++)
        {
            if(players[i].getPoints() > MAX_POINTS)
            {
                System.out.println(players[i].getName() + " busted out");
            }
            else if(croupierHand.getPoints() > MAX_POINTS)
            {
                System.out.println(players[i].getName() + " wins because croupier busted out");
                wins[i]++;
            }
            else if (players[i].getPoints() > croupierHand.getPoints())
            {
                System.out.println(players[i].getName() + " wins");
                wins[i]++;
            }
            else
            {
                System.out.println(players[i].getName() + " loses");
            }
        }
    }

    private void gameFinish()
    {
        System.out.println("Final result:");
        System.out.println();
        for(int i=0; i< NUM_OF_PLAYERS; i++)
        {
            System.out.println(players[i].getName() + " has " + wins[i] + " wins");
        }
        System.out.println();
        System.out.println();
        System.out.println("ENDGAME");
    }

}
