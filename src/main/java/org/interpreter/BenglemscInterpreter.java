package org.interpreter;

import org.antlr.generated.*;
import org.model.Buoy;
import org.model.Satellite;
import org.simulation.World;
import org.strategy.MovementStrategy;
import org.strategy.movement.*;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BenglemscInterpreter extends BenglemscBaseVisitor<Object> {

    private final Map<String, Object> variables = new HashMap<>();
    private final World world;

    public BenglemscInterpreter(World world) {
        this.world = world;
    }

    @Override
    public Object visitProgram(BenglemscParser.ProgramContext ctx) {
        for (BenglemscParser.StatementContext statementContext : ctx.statement()) {
            this.visit(statementContext);
        }
        return null;
    }

    @Override
    public Object visitVarAssignmentStmt(BenglemscParser.VarAssignmentStmtContext ctx) {
        return this.visit(ctx.varAssignment());
    }

    @Override
    public Object visitVarAssign(BenglemscParser.VarAssignContext ctx) {
        String varName = ctx.ID().getText();
        Object value = this.visit(ctx.expr());
        this.variables.put(varName, value);
        System.out.println("Variable '" + varName + "' assigned: " + value);
        return value;
    }

    @Override
    public Object visitNewExpr(BenglemscParser.NewExprContext ctx) {
        String className = ctx.ID().getText();
        Object[] args = null;

        if (ctx.argList() != null) {
            args = (Object[]) this.visit(ctx.argList());
        }

        try {
            return this.createObject(className, args);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de l'objet " + className + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Object visitArgListExpr(BenglemscParser.ArgListExprContext ctx) {
        return ctx.expr().stream().map(this::visit).toArray();
    }

    @Override
    public Object visitIdExpr(BenglemscParser.IdExprContext ctx) {
        String varName = ctx.ID().getText();
        if (!this.variables.containsKey(varName)) {
            throw new RuntimeException("Variable non définie: " + varName);
        }
        return this.variables.get(varName);
    }

    @Override
    public Object visitIntExpr(BenglemscParser.IntExprContext ctx) {
        return Integer.parseInt(ctx.INT().getText());
    }

    @Override
    public Object visitMethodCallStmt(BenglemscParser.MethodCallStmtContext ctx) {
        return this.visit(ctx.methodCall());
    }

    @Override
    public Object visitMethodCallExpr(BenglemscParser.MethodCallExprContext ctx) {
        String objName = ctx.ID(0).getText();
        String methodName = ctx.ID(1).getText();

        if (!this.variables.containsKey(objName)) {
            throw new RuntimeException("Objet non défini: " + objName);
        }

        Object obj = this.variables.get(objName);
        Object[] args = null;

        if (ctx.argList() != null) {
            args = (Object[]) this.visit(ctx.argList());
        }

        try {
            return this.invokeMethod(obj, methodName, args);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'appel de méthode " + methodName + ": " + e.getMessage(), e);
        }
    }

    private Object createObject(String className, Object[] args) throws Exception {
        return switch (className) {
            case "Buoy" -> this.createBuoy(args);
            case "Satellite" -> this.createSatellite(args);
            case "HorizontalMovement" -> new HorizontalMovement(this.world.getContext(), (Integer) args[0]);
            case "SinusMovement" -> new SinusMovement(this.world.getContext(), (Integer) args[0]);
            case "HorizontalMovementSatellite" ->
                    new HorizontalMovementSatellite(this.world.getContext(), (Integer) args[0]);
            default -> throw new RuntimeException("Classe inconnue: " + className);
        };
    }

    private Buoy createBuoy(Object[] args) throws Exception {
        int width = (Integer) args[0];
        int maxData = (Integer) args[1];
        int x = (Integer) args[2];
        int y = (Integer) args[3];
        MovementStrategy movementStrategy = (MovementStrategy) args[4];

        return this.world.createBuoy(width, maxData, x, y, movementStrategy);
    }

    private Satellite createSatellite(Object[] args) throws Exception {
        int width = (Integer) args[0];
        int x = (Integer) args[1];
        int y = (Integer) args[2];
        MovementStrategy movementStrategy = (MovementStrategy) args[3];

        return this.world.createSatellite(width, x, y, movementStrategy);
    }

    private Object invokeMethod(Object obj, String methodName, Object[] args) throws Exception {
        Class<?> clazz = obj.getClass();

        if (args == null || args.length == 0) {
            return clazz.getMethod(methodName).invoke(obj);
        } else {
            Class<?>[] paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paramTypes[i] = args[i].getClass();
                if (paramTypes[i] == Integer.class) {
                    paramTypes[i] = int.class;
                }
            }

            Method method = this.findMethod(clazz, methodName, paramTypes);
            if (method == null) {
                throw new NoSuchMethodException("Méthode " + methodName + " non trouvée");
            }

            return method.invoke(obj, args);
        }
    }

    private Method findMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName) &&
                        method.getParameterCount() == paramTypes.length) {
                    return method;
                }
            }
            return null;
        }
    }
}