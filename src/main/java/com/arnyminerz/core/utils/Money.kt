package com.arnyminerz.core.utils

/**
 * Divides the given amount of money into the least amount possible of packages given.
 * @param amount The amount to divide
 * @param availablePackages The packages of money available to fit. Keys are the identifier of the package, and the
 * value their respective monetary values.
 * @param fillNulls If `true`, all the ids that doesn't have any value (they don't fit), will be filled with `0`s.
 * @return A map with the ids of [availablePackages] as keys, and the amount of packages required
 * as values.
 */
fun <Key: Any> divideMoney(
    amount: Double,
    availablePackages: List<Pair<Key, Double>>,
    fillNulls: Boolean = false,
): Map<Key, Int> {
    // Sort the available packages from greatest to lowest
    val packages = availablePackages.sortedByDescending { (_, price) -> price }
    // Create a map in which all the counts will be stored
    val map = mutableMapOf<Key, Int>()
    // Create a counter that will store the remaining money to sort
    // Note that we cut amount to two decimals
    var remaining = amount.roundTo(2)
    // Start dividing
    while (remaining.roundTo(2) > 0.0)
        for ((id, price) in packages) {
            // If package doesn't fit, go to the next smallest one
            if (price > remaining) continue
            // Get the number of packages we can make
            val packagesCount = (remaining / price).toInt()
            // Update the amount of packages to return
            map[id] = map.getOrDefault(id, 0) + packagesCount
            // Subtract the amount already counted
            remaining -= price * packagesCount
        }
    if (fillNulls)
        for ((id, _) in packages)
            if (!map.containsKey(id))
                map[id] = 0
    return map
}
