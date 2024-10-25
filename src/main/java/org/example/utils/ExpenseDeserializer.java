package org.example.utils;

import com.google.gson.*;
import org.example.model.Expense;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDeserializer implements JsonDeserializer<Expense> {

    @Override
    public Expense deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Long categoryId = jsonObject.get("categoryId").getAsLong();
        Long paymentId = jsonObject.get("paymentId").getAsLong();
        String description = jsonObject.get("description").getAsString();
        BigDecimal amount = jsonObject.get("amount").getAsBigDecimal();
        LocalDate transactionDate = LocalDate.parse(jsonObject.get("transactionDate").getAsString());

        Expense expense = new Expense(categoryId, paymentId, description, amount, transactionDate);

        if(jsonObject.get("id") != null) {
            expense.setId(jsonObject.get("id").getAsLong());
        }

        return expense;
    }
}
