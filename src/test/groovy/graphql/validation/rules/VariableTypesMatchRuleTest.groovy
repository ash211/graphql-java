package graphql.validation.rules

import graphql.Scalars
import graphql.StarWarsSchema
import graphql.language.OperationDefinition
import graphql.language.StringValue
import graphql.language.TypeName
import graphql.language.VariableDefinition
import graphql.language.VariableReference
import graphql.validation.ValidationContext
import graphql.validation.ValidationErrorCollector
import graphql.validation.ValidationErrorType
import spock.lang.Specification

class VariableTypesMatchRuleTest extends Specification {

    ValidationContext validationContext = Mock(ValidationContext)
    ValidationErrorCollector errorCollector = new ValidationErrorCollector()
    VariableTypesMatchRule variableTypesMatchRule

    def setup() {
        variableTypesMatchRule = new VariableTypesMatchRule(validationContext, errorCollector, Mock(VariablesTypesMatcher))
    }

    def "invalid type"() {
        given:
        def defaultValue = new StringValue("default")
        def astType = new TypeName("String")
        def expectedType = Scalars.GraphQLBoolean

        validationContext.getSchema() >> StarWarsSchema.starWarsSchema
        validationContext.getInputType() >> expectedType
        variableTypesMatchRule.variablesTypesMatcher
                .doesVariableTypesMatch(Scalars.GraphQLString, defaultValue, expectedType) >> false

        when:
        variableTypesMatchRule.checkOperationDefinition(new OperationDefinition())
        variableTypesMatchRule.checkVariableDefinition(new VariableDefinition("var", astType, defaultValue))
        variableTypesMatchRule.checkVariable(new VariableReference("var"))

        then:
        errorCollector.containsValidationError(ValidationErrorType.VariableTypeMismatch)


    }
}
