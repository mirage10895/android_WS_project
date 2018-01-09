package fr.eseo.dis.amiaudluc.pfeproject.jurys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.Window;

import fr.eseo.dis.amiaudluc.pfeproject.Content.Content;
import fr.eseo.dis.amiaudluc.pfeproject.R;
import fr.eseo.dis.amiaudluc.pfeproject.common.ItemInterface;
import fr.eseo.dis.amiaudluc.pfeproject.data.model.Jury;
import fr.eseo.dis.amiaudluc.pfeproject.subjects.MySubjectsFragment;
import fr.eseo.dis.amiaudluc.pfeproject.subjects.SubjectActivity;
import fr.eseo.dis.amiaudluc.pfeproject.subjects.SubjectsAdapter;

public class JuryActivity extends AppCompatActivity implements ItemInterface{

    private SubjectsAdapter subjectsAdapter;

    Jury currentJury = Content.jury;

    private boolean loaded = false;
    private String TAG = MySubjectsFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.layout_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.emptyField));
            if (currentJury.getIdJury() != -1) {
                actionBar.setTitle("Jury n°"+Content.jury.getIdJury());
            }
        }

        Content.projects = currentJury.getProject();

        RecyclerView recycler = (RecyclerView) findViewById(R.id.cardList);
        recycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);
        subjectsAdapter = new SubjectsAdapter(this,this);
        recycler.setAdapter(subjectsAdapter);

        loadAllSubjects();
    }

    private void loadAllSubjects(){
        subjectsAdapter.setMySubjects(Content.projects);
        subjectsAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(int position) {
        Content.project = Content.projects.get(position);
        Intent intent = new Intent(this, SubjectActivity.class);
        startActivity(intent);
    }
}
