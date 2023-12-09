package cc.unitmesh.example;

import cc.unitmesh.pick.SimpleCodePicker;
import cc.unitmesh.pick.config.BuilderConfig;
import cc.unitmesh.pick.config.PickerOption;
import cc.unitmesh.pick.prompt.Instruction;
import cc.unitmesh.pick.prompt.InstructionType;
import cc.unitmesh.quality.CodeQualityType;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        List<InstructionType> builderTypes = new ArrayList<>();
        builderTypes.add(InstructionType.RELATED_CODE_COMPLETION);

        List<CodeQualityType> codeQualityTypes = new ArrayList<>();
        codeQualityTypes.add(CodeQualityType.BadSmell);
        codeQualityTypes.add(CodeQualityType.JavaService);

        PickerOption pickerOption = new PickerOption(
                "https://github.com/unit-mesh/unit-eval-testing", "master", "java",
                ".", builderTypes, codeQualityTypes, new BuilderConfig()
        );

        SimpleCodePicker simpleCodePicker = new SimpleCodePicker(pickerOption);
        List<Instruction> output = simpleCodePicker.blockingExecute();

        System.out.println(output);
    }
}
