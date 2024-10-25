package org.example.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.example.model.Expense;

import java.lang.reflect.Type;

public class ExpenseSerializer implements JsonSerializer<Expense> {

    @Override
    public JsonElement serialize(Expense expense, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", expense.getId());

        JsonObject project = new JsonObject();
        project.addProperty("id", expense.getProject().getId());
        project.addProperty("name", expense.getProject().getName());
        jsonObject.add("project", project);

        JsonObject category = new JsonObject();
        category.addProperty("id", expense.getCategory().getId());
        category.addProperty("name", expense.getCategory().getName());
        category.addProperty("description", expense.getCategory().getDescription());
        jsonObject.add("category", category);

        JsonObject payment = new JsonObject();
        payment.addProperty("id", expense.getPaymentMethod().getId());
        payment.addProperty("name", expense.getPaymentMethod().getName());
        payment.addProperty("description", expense.getPaymentMethod().getDescription());
        jsonObject.add("paymentMethod", payment);

        jsonObject.addProperty("description", expense.getDescription());
        jsonObject.addProperty("amount", expense.getAmount());
        jsonObject.addProperty("transactionDate", expense.getTransactionDate().toString());

        return jsonObject;
    }
}
