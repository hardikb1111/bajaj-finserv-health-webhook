package com.bajaj.webhook.service;

import org.springframework.stereotype.Service;

@Service
public class SqlSolutionService {

    public String getSqlSolution(String regNo) {

        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        int lastTwoDigitsInt = Integer.parseInt(lastTwoDigits);

        if (lastTwoDigitsInt % 2 == 1) {

            return getQuestion1Solution();
        } else {

            return getQuestion2Solution();
        }
    }

    private String getQuestion1Solution() {
        return """
            SELECT 
                p.AMOUNT as SALARY,
                CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) as NAME,
                TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) as AGE,
                d.DEPARTMENT_NAME
            FROM PAYMENTS p
            JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
            JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            WHERE DAY(p.PAYMENT_TIME) != 1
            ORDER BY p.AMOUNT DESC
            LIMIT 1
            """;
    }

    private String getQuestion2Solution() {
        // Placeholder for Question 2 - replace with actual solution if needed
        return """
            SELECT 
                c.customer_id,
                c.customer_name,
                COUNT(o.order_id) as total_orders,
                SUM(o.total_amount) as total_spent
            FROM customers c
            JOIN orders o ON c.customer_id = o.customer_id
            WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
            GROUP BY c.customer_id, c.customer_name
            HAVING total_orders > 5
            ORDER BY total_spent DESC
            LIMIT 10
            """;
    }
}