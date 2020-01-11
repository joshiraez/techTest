# Technical test memo

I'll be using this Readme.md as a demo of sorts to annotate any design decisions or other important stuff.

## How to run

Required: 
* Java 11 or above
* Have `customers.csv`, `orders.csv`, `products.csv` on the `main/resources` directory 

To run, cd to project root, then:
* `gradlew build`
* `java -jar build/libs/interview-1.0.jar`

To run tests, cd to project root, then:
* `gradlew test`

## Design decisions

* I try to avoid as much as possible to load the files in memory (csv files are tipically very heavy)
  * But I don't do so with the structure I pass to the write CSV method to not make the code too messy
  * If the results get too memory heavy, it will have to be modified to calculate and write together in the loop, while trying to have the needed data already loaded (like product prices)
    * Another optimization would be to have any of these two restrictions on the CSV: be indexed, or restricted to not be able to erase records (so all ids are in order and place). Then we could do Random memory access to the files and it could be much quicker
      * But right now, I settled for this. Those are nice to have in mind if we found performance problems in the future.
* Using BigDecimal for precision decimal operations. Is much slower than just double types, it depends on the size of the files we will use.
* Many corner cases were not tested. For example: same product. Products not existing... Because they either were irrecoverable or it was very strange they could bug from the code logic.
  * Too many tests to maximixe coverage can get unmaintenable for almost no value (very strange cases).
  * If those corner cases bugged sometime, then we could make a test for that bug in particular.
* After finishing, decided to divide the huge calculator class in various classes for each task
  * We ensure a common point of maintenance in case we need to change the behaviour of some class

## Backlog decisions

I did the project using TDD, so it was kept rather minimalistic. I then refactored the things I saw were the most value, but there are many things that are improvable, just didn't seem as valuable.
This list is that list of TODOs, things that can be added to aid the project but didn't seem critical at the moment.

* Decided to leave the error handling and file validation tests after having the 3 tasks finished. 
  * These are unrecoverable failures, so there is no great value on having them controlled at first.
* Each file generation can be separated to a new class, because they will be maintained in its own.
* Sorting the different data structures to ids, in those where it was not specifically required
  * Task3 NEEDS sorting. This is already don.
  * But Task 1 by order id
  * And Task 2 by product id, and the customers by id
* Putting the Money qtys in a "Money" class of sorts
  * To have a single point of maintenance to all the Money functions/reducers/formatting
  * Instead of changing every BigDecimal ocurrence.
* Put the data extractors of the files in another class. I didn't do it yet because is not huge enough to bring much value.
* Some of the utils class methods are error prone and should be transformed from splitted List to dtos to get type safety when using them.

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

