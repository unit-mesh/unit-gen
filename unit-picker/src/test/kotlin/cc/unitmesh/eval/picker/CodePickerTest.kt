package cc.unitmesh.eval.picker;

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CodePickerTest {
    @Test
    fun should_return_unique_path() {
        // based on: https://www.debuggex.com/r/fFggA8Uc4YYKjl34
        assertEquals(
            "github.com/unit-mesh/unit-eval",
            CodePicker.gitUrlToPath("https://github.com/unit-mesh/unit-eval")
        )
        assertEquals(
            "bitbucket.org/moylop260/odoo-mexico",
            CodePicker.gitUrlToPath("https://bitbucket.org/moylop260/odoo-mexico.git")
        )
        assertEquals(
            "bitbucket.org/moylop260/odoo-mexico",
            CodePicker.gitUrlToPath("git@bitbucket.org:moylop260/odoo-mexico.git")
        )
        assertEquals(
            "github.com/moylop_260/odoo_mexico",
            CodePicker.gitUrlToPath("git@github.com:moylop_260/odoo_mexico.git/")
        )
        assertEquals(
            "github.com/odoo-mexico/odoo_mexico",
            CodePicker.gitUrlToPath("git@github.com:odoo-mexico/odoo_mexico.git/")
        )
        assertEquals(
            "github.com/odoo-mexico/odoo-mexico",
            CodePicker.gitUrlToPath("git@github.com:odoo-mexico/odoo-mexico.git/")
        )
        assertEquals("github.com/odoo/odoo", CodePicker.gitUrlToPath("https://github.com/odoo/odoo"))
        assertEquals(
            "github.com/odoo/odoo",
            CodePicker.gitUrlToPath("https://github.com/odoo/odoo.git")
        )
        assertEquals("github.com/odoo/odoo", CodePicker.gitUrlToPath("git@github.com:odoo/odoo.git"))
        assertEquals("github.com/odoo/odoo", CodePicker.gitUrlToPath("https://github.com/odoo/odoo"))
        assertEquals(
            "bitbucket.org/jespern/django-piston",
            CodePicker.gitUrlToPath("https://bitbucket.org/jespern/django-piston")
        )
        assertEquals(
            "bitbucket.org/wiredesignz/codeigniter-modular-extensions-hmvc",
            CodePicker.gitUrlToPath("https://bitbucket.org/wiredesignz/codeigniter-modular-extensions-hmvc")
        )
        assertEquals(
            "bitbucket.org/dhellmann/virtualenvwrapper-hg",
            CodePicker.gitUrlToPath("https://bitbucket.org/dhellmann/virtualenvwrapper-hg")
        )
        assertEquals(
            "bitbucket.org/zzzeek/alembic_moved_from_hg_to_git",
            CodePicker.gitUrlToPath("https://bitbucket.org/zzzeek/alembic_moved_from_hg_to_git")
        )
    }

    @Test
    fun shouldCheckoutTestCode() {
        val picker = CodePicker(
            PickerConfig(
                url = "https://github.com/unit-mesh/unit-eval-testing"
            )
        )

        picker.run()
    }
}