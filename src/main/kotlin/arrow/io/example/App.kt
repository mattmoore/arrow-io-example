package arrow.io.example

import arrow.core.Either
import arrow.fx.IO
import arrow.fx.extensions.fx

// With regular nested IO Monad - aka IO {}
// What's happening:
// As each monad is run, flatMap returns the success case.
// If a given monad fails to return a value (an exception occurs), the remaining
//   ones don't continue.
fun getCustomerAddressFromOrder(orderId: Int) =
    getOrder(orderId).flatMap { order ->
        getCustomer(order.customerId).flatMap { customer ->
            getAddress(customer.addressId)
        }
    }

// With IO Monad Comprehensions - aka IO.fx {}
// What's happening:
// Same thing as the regular nested monads except IO.fx is extra syntactical
//   sugar that allows us to get the value of each monad on each line, making
//   our code look more "imperative". This can be easier to read, rather than
//   nesting each monad inside flatMap for as many times as there are monads.
fun getCustomerAddressFromOrderComprehensions(orderId: Int) = IO.fx {
    val order = !getOrder(orderId)
    val customer = !getCustomer(order.customerId)
    val address = !getAddress(customer.addressId)
    address
}

fun main(args: Array<String>) {
    // Run regular IO monad with nested monads
    val addressFromNestedMonads = getCustomerAddressFromOrder(1).unsafeRunSync()
    println(addressFromNestedMonads)

    // Run monad comprehensions
    val addressFromComprehensions =
        getCustomerAddressFromOrderComprehensions(1).unsafeRunSync()
    println(addressFromComprehensions)

    // We combine nested monads with an Either<Left, Right> to have contextual
    //   final behavior.
    // In other words, the message we print can change based on whether
    //   the sequence of monads succeeded or failed.
    getCustomerAddressFromOrder(1).attempt().map {
        when (it) {
            is Either.Left  -> println("Not found")
            is Either.Right -> println(it.b)
        }
    }.unsafeRunSync()

    // We can also run monad comprehensions with a
    //   final Either<Left, Right> context.
    getCustomerAddressFromOrderComprehensions(1).attempt().map {
        when (it) {
            is Either.Left  -> println("Not found")
            is Either.Right -> println(it.b)
        }
    }.unsafeRunSync()


    // But what happens if something fails?
    // For example, we pass an invalid order ID?

    // The monads will resolve to nothing,
    //   and nothing plus nothing means nothing.

    // So if an order can't be found, it'll pass nothing to
    //   the getCustomer monad, which in turn returns nothing
    //   and the getAddress monad also receives and returns nothing.

    // This is an effective way to halt processing in the event there's
    //   ever a failure.

    // Using nested IO monads:
    getCustomerAddressFromOrder(-1).attempt().map {
        when (it) {
            is Either.Left  -> println("Not found")
            is Either.Right -> println(it.b)
        }
    }.unsafeRunSync()

    // Using monad comprehensions:
    getCustomerAddressFromOrderComprehensions(-1).attempt().map {
        when (it) {
            is Either.Left  -> println("Not found")
            is Either.Right -> println(it.b)
        }
    }.unsafeRunSync()
}
