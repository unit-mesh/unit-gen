package cc.unitmesh.pick.builder.unittest.typescript;

import cc.unitmesh.core.completion.CompletionBuilderType
import cc.unitmesh.core.unittest.TestCodeBuilderType
import cc.unitmesh.pick.builder.unittest.base.BasicTestIns
import cc.unitmesh.pick.builder.unittest.java.JavaTestCodeService
import cc.unitmesh.pick.ext.buildSourceCode
import cc.unitmesh.pick.option.InsOutputConfig
import cc.unitmesh.pick.threshold.InsQualityThreshold
import cc.unitmesh.pick.worker.job.InstructionFileJob
import cc.unitmesh.pick.worker.job.JobContext
import cc.unitmesh.quality.CodeQualityType
import chapi.ast.javaast.JavaAnalyser
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.archguard.scanner.analyser.count.FileJob
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class JavaTestCodeServiceE2ETest {
    @Test
    fun shouldReturnThreeInsWhenBuild() {
        // Given
        val testCode = """package com.rest.springbootemployee;

import com.rest.springbootemployee.entity.Company;
import com.rest.springbootemployee.entity.Employee;
import com.rest.springbootemployee.repository.CompanyMongoRepository;
import com.rest.springbootemployee.service.CompanyService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class CompanyServiceTest {

    @Mock
    CompanyMongoRepository companyMongoRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    public void should_return_all_companies_when_find_all_given_companies(){
        //given
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));

        Company company1 = new Company(new ObjectId().toString(), "Spring", employees1);
        Company company2 = new Company(new ObjectId().toString(), "Boot", employees2);

        List<Company> companies = new ArrayList<>(Arrays.asList(company1,company2));

        given(companyMongoRepository.findAll()).willReturn(companies);

        //when
        List<Company> actualCompanies = companyService.findAll();

        //then
        assertThat(actualCompanies, hasSize(2));
        assertThat(actualCompanies.get(0), equalTo(company1));
        assertThat(actualCompanies.get(1), equalTo(company2));
    }

    @Test
    public void should_return_company_when_update_given_a_company(){
        //given
        String companyName = "POL";
        List<Employee> employees1 = new ArrayList<>();
        employees1.add(new Employee(String.valueOf(1), "lili", 20, "Female", 2000));
        employees1.add(new Employee(String.valueOf(2), "coco", 10, "Female", 8000));

        List<Employee> employees2 = new ArrayList<>();
        employees2.add(new Employee(String.valueOf(3), "aaa", 20, "Male", 2000));
        employees2.add(new Employee(String.valueOf(4), "bbb", 10, "Male", 8000));

        Company originalCompany = new Company(new ObjectId().toString(), "Spring", employees1);
        Company toUpdateCompany = new Company(new ObjectId().toString(), companyName, employees2);

        String id = originalCompany.getId();
        given(companyMongoRepository.findById(id)).willReturn(Optional.of(originalCompany));
        given(companyMongoRepository.save(originalCompany)).willReturn(toUpdateCompany);

        //when
        Company actualCompany = companyService.update(id, toUpdateCompany);

        //then
        verify(companyMongoRepository).findById(id);
        assertThat(actualCompany.getName(), equalTo(companyName));
    }
}
"""
        val underTestCode = """package com.rest.springbootemployee.service;

import com.rest.springbootemployee.entity.Company;
import com.rest.springbootemployee.exception.NoCompanyFoundException;
import com.rest.springbootemployee.repository.CompanyMongoRepository;
import com.rest.springbootemployee.entity.Employee;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {
    private CompanyMongoRepository companyMongoRepository;

    public CompanyService(CompanyMongoRepository companyMongoRepository) {
        this.companyMongoRepository = companyMongoRepository;
    }

    public List<Company> findAll() {
        return companyMongoRepository.findAll();
    }

    public Company update(String companyId, Company toUpdateCompany) {
        Company existingCompany = companyMongoRepository.findById(companyId)
                .orElseThrow(NoCompanyFoundException::new);
        if (toUpdateCompany.getName() != null) {
            existingCompany.setName(toUpdateCompany.getName());
        }
        return companyMongoRepository.save(existingCompany);
    }
}
"""


        val underTestContainer = JavaAnalyser().analysis(underTestCode, "CompanyService.java")
        underTestContainer.buildSourceCode(underTestCode.lines())

        val testCodeContainer = JavaAnalyser().analysis(testCode, "CompanyServiceTest.java")
        testCodeContainer.buildSourceCode(testCode.lines())

        val testFileJob = InstructionFileJob(
            FileJob(
            ),
            codeLines = testCode.lines(),
            code = testCode,
            container = testCodeContainer
        )
        val underTestJob = InstructionFileJob(
            FileJob(
            ),
            codeLines = underTestCode.lines(),
            code = underTestCode,
            container = underTestContainer
        )

        val context = JobContext(
            job = testFileJob,
            qualityTypes = listOf(CodeQualityType.JavaController),
            fileTree = hashMapOf(
                "com.rest.springbootemployee.service.CompanyService" to underTestJob,
                "com.rest.springbootemployee.service.CompanyServiceTest" to testFileJob,
            ),
            insOutputConfig = InsOutputConfig(),
            completionBuilderTypes = listOf(CompletionBuilderType.TEST_CODE_GEN),
            maxTypedCompletionSize = 3,
            insQualityThreshold = InsQualityThreshold()
        )

        val builder = JavaTestCodeService(context)

        // When
        val result = builder.build(testCodeContainer.DataStructures[0])

        // Then
        assertEquals(3, result.size)
        val typedTestIns = result[1] as BasicTestIns
        typedTestIns.testType shouldBe TestCodeBuilderType.METHOD_UNIT
        typedTestIns.generatedCode shouldContain "@Test"
        typedTestIns.underTestCode shouldContain "companyMongoRepository.findAll()"
    }
}
