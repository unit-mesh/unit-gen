package cc.unitmesh.quality.documentation.rule

/**
 * Parse the documentation of the code and check whether the documentation is complete.
 *
 * For example, the following code is missing the parameter description:
 * ```java
 * /**
 *  * Sum a and b, and return the result.
 *  * @param a the first number
 *  * @return the result of a + b
 * */
 * public int calculateSum(int a, int b) {
 *    return a + b;
 * }
 * ```
 *
 * We can use this rule to check whether the documentation is complete.
 */
class MissingParameterDescRule: DocRule() {

}