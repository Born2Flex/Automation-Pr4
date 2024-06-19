package ua.edu.ukma.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes("ua.edu.ukma.annotations.compile.SimpleBuilder")
@AutoService(Processor.class)
public class CustomProcessor extends AbstractProcessor {
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        annotations.forEach(annotation -> roundEnv.getElementsAnnotatedWith(annotation).forEach(element -> {
            if (element.getKind().isClass()) {
                generateBuilder(element);
            }
        }));
        return true;
    }

    private void generateBuilder(Element element) {
        String className = element.getSimpleName().toString();
        String packageName = element.getEnclosingElement().toString();
        String builderName = className + "Builder";
        List<FieldSpec> fields = getFields(element)
                .stream()
                .map(e -> FieldSpec.builder(TypeName.get(e.asType()), e.getSimpleName().toString(), Modifier.PRIVATE).build())
                .toList();

        List<MethodSpec> methods = new ArrayList<>(fields.stream()
                .map(fieldSpec -> generateSetter(fieldSpec, packageName, builderName))
                .toList());

        methods.add(generateBuildMethod(element, className, fields));

        TypeSpec classBuilder = TypeSpec.classBuilder(builderName)
                .addModifiers(Modifier.PUBLIC)
                .addFields(fields)
                .addMethods(methods)
                .build();
        try {
            JavaFile.builder(packageName, classBuilder).build().writeTo(filer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MethodSpec generateBuildMethod(Element element, String className, List<FieldSpec> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("return new ").append(className).append("(");

        StringJoiner sj = new StringJoiner(", ", "", ")");
        fields.forEach(field -> sj.add(field.name));

        sb.append(sj);

        return MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(element.asType()))
                .addStatement(sb.toString())
                .build();
    }

    private MethodSpec generateSetter(FieldSpec e, String packageName, String builderName) {
        return MethodSpec.methodBuilder(e.name)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(packageName, builderName))
                .addParameter(e.type, e.name)
                .addStatement("this." + e.name + " = " + e.name)
                .addStatement("return this")
                .build();
    }

    private List<? extends Element> getFields(Element element) {
        return element.getEnclosedElements().stream()
                .filter(e -> e.getKind().equals(ElementKind.FIELD))
                .toList();
    }
}
