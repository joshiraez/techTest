# Overview

Choose whatever language you're most comfortable with to solve these problems.

# Exercise

The ACME inc. tool supply company manages its operations with 3 csv files:

1. `customers.csv` keeps customer information:
    * `id` is a numeric customer id
    * `firstname` is the customer's first name
    * `lastname` is the customer's last name
2. `products.csv` keeps product info:
    * `id` is a numeric product id
    * `name` is the human-readable name
    * `cost` is the product cost in euros
3. `orders.csv` keeps order information:
    * `id` is a numeric order id
    * `customer` is the numeric id of the customer who created the order
    * `products` is a space-separated list of product ids ordered by the customer

Manually dealing with those files is hard and error-prone, and they've asked for your help writing some code to make their lives easier.

### Task 1

Right now the `orders.csv` doesn't have total order cost information.

We need to use the data in these files to emit a `order_prices.csv` file with the following columns:
* `id` the numeric id of the order
* `euros` the total cost of the order

### Task 2

The marketing department wants to know which customers are interested in each product; they've asked for a `product_customers.csv` file that, for each product, gives the list of customers who have purchased this product:
* `id` numeric product id
* `customer_ids` a space-separated list of customer ids of the customers who have purchased this product

### Task 3

To evaluate our customers, we need a `customer_ranking.csv` containing the following columns, ranked in descending order by total_euros:
* `id` numeric id of the customer
* `firstname` customer first name
* `lastname` customer last name
* `total_euros` total euros this customer has spent on products