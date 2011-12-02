package com.metservice.kanban.jwebunit;

import static com.metservice.kanban.KanbanService.KANBAN_HOME_PROPERTY_NAME;
import static com.metservice.kanban.jwebunit.BoardPage.openProject;
import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import com.metservice.kanban.tests.util.TestUtils;

public class EndToEndTest {

    private static Server server;

    private static TemporaryFolder kanbanHome = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws Exception {
        kanbanHome.create();
        System.setProperty(KANBAN_HOME_PROPERTY_NAME, kanbanHome.getRoot().getAbsolutePath());
        server = new Server(8008);
        server.setHandler(new WebAppContext("src/main/webapp", "/kanban"));
        server.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stop();
        kanbanHome.delete();
    }

    @Test
    public void userCanDeleteAWorkItem() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        //        page.clickBacklogButton();
        page.clickEditFeatureButton("feature name").clickDeleteButtonAndConfirm();

        page.assertFeatureNotPresent("feature name");
    }
    
    @Test
    public void petPageShowsPlannedFeatures() throws IOException{
    	BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
    	page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
    	page.clickPETButton().checkFeatureDescription("feature name");
    }
    
    @Test
    public void petPageShowsPlannedStoriesForProjectsWithOnlyStories() throws IOException{
    	BoardPage page = openProject(kanbanHome, "Test project 2", "/Test Project 2/");
    	page.clickAddStoryButton().enterName("feature name").clickSaveButton();
    	page.clickPETButton().checkFeatureDescription("feature name");
    }
    
    @Test
    public void testProjectListAppearsSorted() throws IOException{
    	File root = kanbanHome.getRoot();
    	BoardPage.cleanProject(kanbanHome);
    	TestUtils.createTestProject(root, "123", "/Test Project 2/");
    	TestUtils.createTestProject(root, "ABC", "/Test Project 2/");
    	TestUtils.createTestProject(root, "XYZ", "/Test Project 2/");
    	TestUtils.createTestProject(root, "acb", "/Test Project 2/");
    	TestUtils.createTestProject(root, "xzy", "/Test Project 2/");
    	TestUtils.createTestProject(root, "A1z", "/Test Project 2/");
    	BoardPage.createBoardPage("123").assertProjectListIsSorted();
    }

    @Test
    public void userCanAddSizeToAWorkItem() throws IOException{
    	BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").enterAverageCase("5").enterWorstCase("10").clickSaveButton();
        //Check that the size on the feature or story matches the Average Case value in P.E.T
        page.clickPETButton().checkPetAverageCaseValue("5").checkPetWorstCaseValue("10");
    }
    
    @Test
    public void userCanRenameAWorkItem() throws IOException {
    	BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickEditFeatureButton("feature name").enterName("new feature name").clickSaveButton();

        page.assertFeatureNotPresent("feature name");
        page.assertFeatureIsPresent("new feature name");
    }

    @Test
    public void userCanExcludeAWorkItemFromReports() throws IOException {
    	BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature name").clickSaveButton();
        page.clickEditFeatureButton("feature name").assertExcludeBoxIs(false);
        page.clickBacklogButton();
        page.clickEditFeatureButton("feature name").tickExcludeBox().clickSaveButton();
        page.clickEditFeatureButton("feature name").assertExcludeBoxIs(true);
    }

    @Test
    public void userCanChangeTheParentOfAWorkItem() throws IOException {
    	BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickBacklogButton();
        page.clickAddFeatureButton().enterName("feature 1").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature 2").clickSaveButton();
        page.clickAdvance("feature 1");
        page.clickAdvance("feature 2");
        WallPage wall = page.clickWallButton();
//        
//        wall.clickAddStoryButton("feature 1").enterName("story").clickSaveButton();
//        
//        wall.clickEditStoryButton("story").setParent("feature 2").clickSaveButton();
        //27/09/11 - feature 2 never gets to the wall. TODO: Ask Ben!
        //wall.clickEditStoryButton("story").assertParentIs("feature 2");
    }

    @Test
    public void userCanViewAChart() throws IOException {
    	BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
//        ChartPage chartPage = wallPage.clickFeatureCycleTimeChartButton();
//        chartPage.assertImageIsValidPng("cycle-time-chart.png?level=feature");

    }

    @Test
    public void userCanViewABurnUpChart() throws IOException {
    	BoardPage wallPage = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        ChartPage chartPage = wallPage.clickBurnUpChartButton();
        chartPage.assertImageIsValidPng("burn-up-chart.png?level=feature&startDate=&endDate=&workStream=");
    }
    
    
    @Test
    public void mustHaveItemShouldBeIndicatedInBacklog() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickAddFeatureButton().enterName("feature name must have").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature name nice to have").clickSaveButton();
        
        BoardPage backlog = page.clickPETButton().clickMustHave(1).clickBacklogButton();
        
        backlog.assertItemNameIsIndicatedMustHave(1);
        backlog.assertItemNameIsIndicatedNiceToHave(2);
    }

    @Test
    @Ignore
    public void mustHaveItemShouldBeIndicatedInComplete() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        page.clickAddFeatureButton().enterName("feature name must have").clickSaveButton();
        page.clickAddFeatureButton().enterName("feature name nice to have").clickSaveButton();

        BoardPage complete = page.clickPETButton().clickMustHave(1).clickCompleteButton();

        complete.assertItemNameIsIndicatedMustHave(1);
        complete.assertItemNameIsIndicatedNiceToHave(2);
    }

    @Test
    public void userCanGoFromEditPageToPrintPage() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");
        PrintPage printPage = page.clickAddFeatureButton().enterName("feature name to print")
            .clickSaveAndPrintButton();

        printPage.assertIsPrintPage();
        printPage.assertItemHasName(1, "feature name to print");
    }

    @Test
    public void quotesAreEscapedOnWall() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");

        page
            .clickAddFeatureButton()
            .enterName("feature name to print")
            .enterNotes("This is \"note\" with.")
            .clickSaveButton()
            .clickAdvance("feature name to print");

        WallPage wallPage = page.clickWallButton();

        System.out.println(wallPage.tester.getPageSource());

        String notes = wallPage.getNotesForItem(1);

        assertEquals("Notes: This is \"note\" with.", notes);
    }

    @Test
    public void htmlEntitiesAreEscapedOnWall() throws IOException {
        BoardPage page = openProject(kanbanHome, "Test project", "/end-to-end-test/");

        WallPage wallPage = page.clickAddFeatureButton().enterName("feature name to print")
            .enterNotes("This is note with <div>HTMLtags</div>.").clickSaveButton()
            .clickAdvance("feature name to print")
            .clickWallButton();

        // if HTML entities are not escaped, this method throws an exception

        //        System.out.println(wallPage.tester.getPageSource());

        String notes = wallPage.getNotesForItem(1);

        assertEquals("Notes: This is note with <div>HTMLtags</div>.", notes);
    }
//    @Test
//    public void userCanDownloadStories() throws IOException {
//        BoardPage wallPage = openProject("Test project");
//        String responseContent = wallPage.clickDownloadFeaturesButton();
//        File expectedFile = new File(SystemUtils.getUserDir(), "/src/test/resources/end-to-end-test/feature.csv");
//        String expectedContent = FileUtils.readFileToString(expectedFile);
//        assertThat(responseContent, is(expectedContent));
//    }
    
//    @Test
//    public void columnRedWhenWIPExceeded() throws IOException {
//    	 BoardPage wallPage = openProject("Test project");
//    	 //assert that th id"phase_2" background-color = #f00
//    	//TODO Get help from someone at MetService for writing tests
//    }
    
    
    
}
