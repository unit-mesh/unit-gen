package cc.unitmesh.example;

import cc.unitmesh.pick.SimpleCodePicker;
import cc.unitmesh.pick.builder.BuilderConfig;
import cc.unitmesh.pick.builder.PickerOption;
import cc.unitmesh.pick.prompt.CodeContextStrategy;
import cc.unitmesh.pick.prompt.CompletionBuilderType;
import cc.unitmesh.pick.prompt.Instruction;
import cc.unitmesh.quality.CodeQualityType;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        List<CodeContextStrategy> instructionTypes = new ArrayList<>();
        instructionTypes.add(CodeContextStrategy.RELATED_CODE);

        // config your code quality types
        List<CodeQualityType> codeQualityTypes = new ArrayList<>();
        codeQualityTypes.add(CodeQualityType.BadSmell);
        codeQualityTypes.add(CodeQualityType.JavaService);

        BuilderConfig builderConfig = new BuilderConfig();

        List<CompletionBuilderType> completionTypes = new ArrayList<>();
        completionTypes.add(CompletionBuilderType.IN_BLOCK_COMPLETION);
        completionTypes.add(CompletionBuilderType.AFTER_BLOCK_COMPLETION);

        PickerOption pickerOption = new PickerOption(
                "https://github.com/unit-mesh/unit-gen-testing", "master", "java",
                ".", instructionTypes, completionTypes, codeQualityTypes, builderConfig
        );

        SimpleCodePicker simpleCodePicker = new SimpleCodePicker(pickerOption);
        List<Instruction> output = simpleCodePicker.blockingExecute();

        System.out.println(output);
    }
}
