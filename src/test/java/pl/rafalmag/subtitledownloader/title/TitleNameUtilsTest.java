package pl.rafalmag.subtitledownloader.title;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junitparams.JUnitParamsRunner.$;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.Assert.assertThat;

@RunWith(JUnitParamsRunner.class)
public class TitleNameUtilsTest {

    @Parameters()
    @Test
    public void should_get_title(String baseName, String expectedTitle)
            throws Exception {
        // given

        // when
        String title = TitleNameUtils.getTitleFrom(baseName);

        // then
        assertThat(title, equalToIgnoringCase(expectedTitle));
    }

    protected Object[] parametersForShould_get_title() {
        return $(
                $("A Lonely Place To Die  {2011} DVDRIP. Jaybob.avi",
                        "A Lonely Place To Die"),
                $("Buried[2010]DvDrip[Eng]-FXG.avi", "Buried"),
                $("Color_of_Night.avi", "Color of night"),
                $("Discworld Terry.Pratchetts.Going.Postal.BRRIP.MP4.x264.720p-HR.avi",
                        "Discworld Terry Pratchetts Going Postal"),
                $("We.Need.To.Talk.About.Kevin.DVDSCR.x264.AAC-Seedpeer.me.avi",
                        "We Need To Talk About Kevin"),
                $("No.Country.For.Old.Men[2007]DvDrip-aXXo.avi",
                        "No Country For Old Men"),
                $("Terra.Nova.S01E01.Genesis.HDTV.XviD-FQM.avi",
                        "Terra Nova S01E01 Genesis"),
                $("Zeitgeist.(2007).[DVDRip]-XviD-MP3.avi", "Zeitgeist"),
                $("Shakespeare In Love [DVD-RIP x264][OGG 2ch EN][Sub BR][Sample].mkv",
                        "Shakespeare In Love"),
                $("Cast Away - Tom Hanks 2000 BR.avi", "Cast Away"),
                $("Insidious.2011.720p.RC.BDRip.XviD.AC3-FLAWL3SS.avi",
                        "Insidious"),
                $("Martha.Marcy.May.Marlene.2011.WORKPRINT.XViD-NFT.avi",
                        "Martha Marcy May Marlene"),
                $("The Girl With A Dragon Tattoo 2011 DVDSCR XviD AC3-FTW.avi",
                        "The Girl With A Dragon Tattoo"),
                $("Mission.Impossible.Ghost.Protocol.2011.1080p.BluRay.x264-SECTOR7 [PublicHD.ORG].mkv",
                        "Mission Impossible Ghost Protocol"));
    }
}
