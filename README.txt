 0) This project is the "Poker Hands" domain, using Java for Interface,
    Platform and Language.
 
 1) Please please please don't ding me on the UI. All of my prior
    UI work in java has been for Android devices (and that UI 
    looks nice). I decided to learn Swing for this project, so
    the UI is admittedly awful. I'm also unsure how this will look
    in other environments...
   
 2) This code runs under java JRE 1.8.0_144

 3) This code is built with javac 1.7.0_79.

 4) This code uses JUnit 4.10.

 5) The init.bat file (once you configure it to use your paths) will 
    initialize your build and run environment. If your JUNIT_HOME and
    CLASSPATH are already set, then you don't need to run this again...

 6) To build, run the build.bat file in Windows.

 7) Code structure:
    a) The "HandEvaluator" takes a string describing a hand, formatted like "KC,4D,KD,JS,10H",
       and returns a HandEvaluator.Value object that can be compared to other instances.
    b) The "TestEvaluator" runs the test harness within HandEvaluator.
         i) It ensures that an InvalidHandException is thrown for invalid hands 
            (bad cards, too many cards, too few cards).
        ii) It checks that the HandEvaluator.Value instances are correct regarding hand type
            and "kicker" values.
       iii) It checks that better hands beat worse hands.
    c) The "UserInterface" displays an admittedly primitive user interface for dealing cards,
       and comparing the resulting hands.
	  
 8) To run the HandEvaluator tests, just "java PokerHand.TestEvaluator".
    This prints out the test descriptions as they run, and then (SHOULD) say
    "ALL TEST PASSED!" when it's done.

 9) To run the UserInterface, just "java PokerHand.UserInterface".

10) In the UserInterface:
    a) Click on a card from the deck to deal it into the next available slot. Once you have
       dealt a card, you cannot deal it again (until you hit "Reset").
    b) When all ten cards have been dealt, you can click on the "Compare" button to see who
       wins. The winning hand will be highlighted in green, and the reason for the win will
       appear at the bottom of the frame. (If the hands tie, it will tell you so).
    c) To restart, click the "Reset" button to start over.
    d) You can edit the player names "in situ", and those values will appear in the "result"
       pane at the bottom when you hit "Compare".
	   
11) Noted TODOs:
    a) The HandEvaluator does not check for duplicate cards. The user interface prevents this
       situation, but we still need to check.
    b) The rules of the project allow for "ties" in some cases. I think that the real rules
       of poker fall back to the suit, but I did not include that (but I did "leave room" for
       it).
    c) The UI also needs a test harness.
    d) There should be some processing on the player names, to keep them from being too long.
	
12) Expected extensions:
    a) More players.
    b) Texas hold-em: find best five cards out of seven, with some number of common cards.
    c) Shuffle and deal, to generate random hands.
	
	
	