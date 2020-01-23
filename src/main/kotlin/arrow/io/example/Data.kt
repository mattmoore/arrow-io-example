package arrow.io.example

import arrow.fx.IO

val addresses = listOf(
  Address(1, "123 Anywhere Street", "Chicago", "IL")
)

val customers = listOf(
  Customer(1, "Matt", "Moore", 1)
)

val orders = listOf(
  Order(1, 1)
)

fun getOrder(id: Int) = IO<Order> {
  orders.find { it.id == id }!!
}

fun getCustomer(id: Int) = IO<Customer> {
  customers.find { it.id == id }!!
}

fun getAddress(id: Int) = IO<Address> {
  addresses.find { it.id == id }!!
}
