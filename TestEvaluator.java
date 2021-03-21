package PokerHand;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


import java.util.*;

import PokerHand.*;

public class TestEvaluator {
   
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(HandEvaluator.class);
		if(result.wasSuccessful()) {
			System.out.println("\n\nTEST PASSED!\n\n");
		}
		else {
			if(result.getFailureCount() == 1) {
				System.out.println("\n\nTHERE WAS 1 FAILURE:\n");
			}
			else {
				System.out.println("\n\nTHERE WERE " + result.getFailureCount() + " FAILURES:\n");
			}
			for(Failure failure: result.getFailures()) {
				System.out.println(failure.toString());
			}
		}
	}
}