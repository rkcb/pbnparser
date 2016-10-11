package com.pbn.parser;

import org.parboiled.Action;
import org.parboiled.Context;

import com.pbn.ast.Pbn;
import com.pbn.ast.PbnObject;

public class Validator implements Action<Pbn> {

    @Override
    public boolean run(Context<Pbn> context) {
        Pbn pbn = context.getValueStack().peek();
        PbnObject o = (PbnObject) pbn;
        int s = o.rows().size();
        int rs = o.rows().get(s - 1).size();

        System.out.println(": " + rs);
        return true;
    }

}
