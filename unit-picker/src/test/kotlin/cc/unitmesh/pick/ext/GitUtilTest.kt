package cc.unitmesh.pick.ext

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitUtilTest {
    @Test
    fun should_return_unique_path() {
        assertEquals(
            "github.com/unit-mesh/unit-gen",
            GitUtil.gitUrlToPath("https://github.com/unit-mesh/unit-gen")
        )
        assertEquals(
            "bitbucket.org/moylop260/odoo-mexico",
            GitUtil.gitUrlToPath("https://bitbucket.org/moylop260/odoo-mexico.git")
        )
        assertEquals(
            "bitbucket.org/moylop260/odoo-mexico",
            GitUtil.gitUrlToPath("git@bitbucket.org:moylop260/odoo-mexico.git")
        )
        assertEquals(
            "github.com/moylop_260/odoo_mexico",
            GitUtil.gitUrlToPath("git@github.com:moylop_260/odoo_mexico.git/")
        )
        assertEquals(
            "github.com/odoo-mexico/odoo_mexico",
            GitUtil.gitUrlToPath("git@github.com:odoo-mexico/odoo_mexico.git/")
        )
        assertEquals(
            "github.com/odoo-mexico/odoo-mexico",
            GitUtil.gitUrlToPath("git@github.com:odoo-mexico/odoo-mexico.git/")
        )
        assertEquals("github.com/odoo/odoo", GitUtil.gitUrlToPath("https://github.com/odoo/odoo"))
        assertEquals(
            "github.com/odoo/odoo",
            GitUtil.gitUrlToPath("https://github.com/odoo/odoo.git")
        )
        assertEquals("github.com/odoo/odoo", GitUtil.gitUrlToPath("git@github.com:odoo/odoo.git"))
        assertEquals("github.com/odoo/odoo", GitUtil.gitUrlToPath("https://github.com/odoo/odoo"))
        assertEquals(
            "bitbucket.org/jespern/django-piston",
            GitUtil.gitUrlToPath("https://bitbucket.org/jespern/django-piston")
        )
        assertEquals(
            "bitbucket.org/wiredesignz/codeigniter-modular-extensions-hmvc",
            GitUtil.gitUrlToPath("https://bitbucket.org/wiredesignz/codeigniter-modular-extensions-hmvc")
        )
        assertEquals(
            "bitbucket.org/dhellmann/virtualenvwrapper-hg",
            GitUtil.gitUrlToPath("https://bitbucket.org/dhellmann/virtualenvwrapper-hg")
        )
        assertEquals(
            "bitbucket.org/zzzeek/alembic_moved_from_hg_to_git",
            GitUtil.gitUrlToPath("https://bitbucket.org/zzzeek/alembic_moved_from_hg_to_git")
        )
    }

    @Test
    fun shouldHandleDotInUrl() {
        val url = "https://github.com/openmrs/openmrs-module-webservices.rest"
        val gitUrlToPath = GitUtil.gitUrlToPath(url)
        assertEquals("github.com/openmrs/openmrs-module-webservices.rest", gitUrlToPath)
    }
}