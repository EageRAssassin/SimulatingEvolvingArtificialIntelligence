package server.interpret;

import java.util.ArrayList;

import server.ast.*;
import server.ast.BinaryCondition.Operator;
import server.ast.Number;
import server.parse.TokenType;
import server.world.Critter;
import server.world.WorldConstants;

public class InterpreterImpl implements Interpreter {
	Critter critter;
	ArrayList<Rule> rules = new ArrayList<Rule>();
	int ruleSetExecuted = 0;

	public InterpreterImpl(Critter critter) {
		this.critter = critter;
		rules = new ArrayList<Rule>(critter.rules.root.rules);
	}

	@Override
	public Outcome interpret(Program p) {
		Rule lastRule = null;
		rules = new ArrayList<Rule>(critter.rules.root.rules);
		ruleSetExecuted = 0;
		critter.mem[5] = 0;
		boolean resetRules = false;
		while (true) {

			if (rules.size() == 0)
				break;

			Rule rule = rules.get(0);
			resetRules = false;

			if (eval(rule.condition)) {

				// Command contains only an action
				if (rule.command.updates.size() == 0) {
					if (rule.command.action.value.getType().equals(TokenType.WAIT))
						critter.waitAction();
					if (rule.command.action.value.getType().equals(TokenType.FORWARD))
						critter.move(true);
					if (rule.command.action.value.getType().equals(TokenType.BACKWARD))
						critter.move(false);
					if (rule.command.action.value.getType().equals(TokenType.LEFT))
						critter.turn(true);
					if (rule.command.action.value.getType().equals(TokenType.RIGHT))
						critter.turn(false);
					if (rule.command.action.value.getType().equals(TokenType.EAT))
						critter.eat();
					if (rule.command.action.value.getType().equals(TokenType.ATTACK))
						critter.attack();
					if (rule.command.action.value.getType().equals(TokenType.GROW))
						critter.grow();
					if (rule.command.action.value.getType().equals(TokenType.BUD))
						critter.bud();
					if (rule.command.action.value.getType().equals(TokenType.MATE))
						critter.mate();
					if (rule.command.action.value.getType().equals(TokenType.TAG))
						critter.tag(eval(rule.command.action.child));
					if (rule.command.action.value.getType().equals(TokenType.SERVE))
						critter.serve(eval(rule.command.action.child));
					critter.mem[5]++;
					StringBuilder sb = new StringBuilder();
					lastRule = rule;
					break;
				}

				// Command contains only updates
				if (rule.command.action == null) {
					for (Update update : rule.command.updates)
						update(update);
					critter.mem[5]++;
					StringBuilder sb = new StringBuilder();
					lastRule = rule;
					ruleSetExecuted++;
					resetRules = true;

				} else { // Command contains updates and an action
					for (Update update : rule.command.updates)
						update(update);
					if (rule.command.action.value.getType().equals(TokenType.WAIT))
						critter.waitAction();
					if (rule.command.action.value.getType().equals(TokenType.FORWARD))
						critter.move(true);
					if (rule.command.action.value.getType().equals(TokenType.BACKWARD))
						critter.move(false);
					if (rule.command.action.value.getType().equals(TokenType.LEFT))
						critter.turn(true);
					if (rule.command.action.value.getType().equals(TokenType.RIGHT))
						critter.turn(false);
					if (rule.command.action.value.getType().equals(TokenType.EAT))
						critter.eat();
					if (rule.command.action.value.getType().equals(TokenType.ATTACK))
						critter.attack();
					if (rule.command.action.value.getType().equals(TokenType.GROW))
						critter.grow();
					if (rule.command.action.value.getType().equals(TokenType.BUD))
						critter.bud();
					if (rule.command.action.value.getType().equals(TokenType.MATE))
						critter.mate();
					if (rule.command.action.value.getType().equals(TokenType.TAG))
						critter.tag(eval(rule.command.action.child));
					if (rule.command.action.value.getType().equals(TokenType.SERVE))
						critter.serve(eval(rule.command.action.child));
					critter.mem[5]++;
					StringBuilder sb = new StringBuilder();
					lastRule = rule;
					break;
				}

			}
			rules.remove(0);
			if (resetRules)
				rules = new ArrayList<Rule>(critter.rules.root.rules);
			if (ruleSetExecuted == WorldConstants.MAX_RULES_PER_TURN)
				return new OutcomeImpl(false, lastRule);
		}
		return new OutcomeImpl(true, lastRule);
	}

	@Override
	public boolean eval(Condition c) {
		boolean result = false;
		if (c instanceof Relation) {
			if (((Relation) c).relOp.getType().equals(TokenType.LT))
				return eval(((Relation) c).left) < eval(((Relation) c).right);
			if (((Relation) c).relOp.getType().equals(TokenType.LE))
				return eval(((Relation) c).left) <= eval(((Relation) c).right);
			if (((Relation) c).relOp.getType().equals(TokenType.EQ))
				return eval(((Relation) c).left) == eval(((Relation) c).right);
			if (((Relation) c).relOp.getType().equals(TokenType.GE))
				return eval(((Relation) c).left) >= eval(((Relation) c).right);
			if (((Relation) c).relOp.getType().equals(TokenType.GT))
				return eval(((Relation) c).left) > eval(((Relation) c).right);
			if (((Relation) c).relOp.getType().equals(TokenType.NE))
				return eval(((Relation) c).left) != eval(((Relation) c).right);
		} else {
			if (((BinaryCondition) c).op.equals(Operator.AND)) {
				result = eval(((BinaryCondition) c).l) && eval(((BinaryCondition) c).r);
				if (((BinaryCondition) c).extraCondition.size() > 0) {
					for (int i = 0; i < ((BinaryCondition) c).extraCondition.size(); i++)
						result = result && eval(((BinaryCondition) c).extraCondition.get(i));
				}
			} else {
				result = eval(((BinaryCondition) c).l) || eval(((BinaryCondition) c).r);
				if (((BinaryCondition) c).extraCondition.size() > 0) {
					for (int i = 0; i < ((BinaryCondition) c).extraCondition.size(); i++)
						result = result || eval(((BinaryCondition) c).extraCondition.get(i));
				}
			}
		}
		return result;
	}

	@Override
	public int eval(Expr e) {
		if (e instanceof ExprGeneral) {
			int result = 0;
			if (((ExprGeneral) e).r == null) {
				return eval(((ExprGeneral) e).l);
			} else {
				if (((ExprGeneral) e).addOperation.equals(TokenType.PLUS))
					result = eval(((ExprGeneral) e).l) + eval(((ExprGeneral) e).r);
				if (((ExprGeneral) e).addOperation.equals(TokenType.MINUS))
					result = eval(((ExprGeneral) e).l) - eval(((ExprGeneral) e).r);
			}
			if (((ExprGeneral) e).extraTerm.size() > 0) {
				for (int i = 0; i < ((ExprGeneral) e).extraTerm.size(); i++) {
					if (((ExprGeneral) e).extraAddOp.get(i).equals(TokenType.PLUS))
						result += eval(((ExprGeneral) e).extraTerm.get(i));
					if (((ExprGeneral) e).extraAddOp.get(i).equals(TokenType.MINUS))
						result -= eval(((ExprGeneral) e).extraTerm.get(i));
				}
			}
			return result;
		}

		if (e instanceof Term) {
			int result2 = 0;
			if (((Term) e).r == null) {
				return eval(((Term) e).l);
			} else {
				if (((Term) e).mulOperation.equals(TokenType.MUL))
					result2 = eval(((Term) e).l) * eval(((Term) e).r);
				if (((Term) e).mulOperation.equals(TokenType.DIV))
					if (eval(((Term) e).r) != 0)
						result2 = eval(((Term) e).l) / eval(((Term) e).r);
					else
						result2 = 0;
				if (((Term) e).mulOperation.equals(TokenType.MOD))
					if (eval(((Term) e).r) != 0)
						result2 = eval(((Term) e).l) % eval(((Term) e).r);
					else
						result2 = 0;
			}
			if (((Term) e).extraFactor.size() > 0) {
				for (int i = 0; i < ((Term) e).extraFactor.size(); i++) {
					if (((Term) e).extraMulOp.get(i).equals(TokenType.MUL))
						result2 *= eval(((Term) e).extraFactor.get(i));
					if (((Term) e).extraMulOp.get(i).equals(TokenType.DIV))
						if (eval(((Term) e).extraFactor.get(i)) != 0)
							result2 /= eval(((Term) e).extraFactor.get(i));
						else
							result2 = 0;
					if (((Term) e).extraMulOp.get(i).equals(TokenType.MOD))
						if (eval(((Term) e).extraFactor.get(i)) != 0)
							result2 = result2 % eval(((Term) e).extraFactor.get(i));
						else
							result2 = 0;
				}
			}
			return result2;
		}

		if (e instanceof Factor) {
			if (((Factor) e).isNegative == true)
				return -eval(((Factor) e).child);
			else
				return eval(((Factor) e).child);
		}

		if (e instanceof Number) {
			return ((Number) e).value.getValue();
		}

		if (e instanceof Memory) {
			int memNum = eval(((Memory) e).value);
			return critter.getMem(memNum);
		}

		if (e instanceof Sensor) {
			if (((Sensor) e).value.getType().equals(TokenType.NEARBY))
				return critter.nearby(eval(((Sensor) e).child));
			if (((Sensor) e).value.getType().equals(TokenType.AHEAD))
				return critter.ahead(eval(((Sensor) e).child));
			if (((Sensor) e).value.getType().equals(TokenType.RANDOM))
				return critter.random(eval(((Sensor) e).child));
			if (((Sensor) e).value.getType().equals(TokenType.SMELL))
				return critter.smell();
		}

		return 0;
	}

	/**
	 * update the critter by using the update node passed to it
	 *
	 * @param update
	 *            the update node for the critter to interpret and update
	 */
	public void update(Update update) {
		try {
			critter.update(eval(update.exprInMem), eval(update.exprGiven));
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			update.exprInMem.prettyPrint(sb);
			int memvalue = eval(((Memory) ((ExprGeneral) update.exprInMem).l.l.child).value);
			critter.update(memvalue, eval(update.exprGiven));
		}
	}
}
